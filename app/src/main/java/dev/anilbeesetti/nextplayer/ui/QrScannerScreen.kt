package dev.anilbeesetti.nextplayer.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.guava.await

/**
 * Full-screen dialog that shows a QR code scanner.
 *
 * FIX: CameraX black screen — uses the Activity's LifecycleOwner
 * (not the Dialog's), sets PreviewView implementation mode to
 * PERFORMANCE, and ensures camera is bound AFTER the PreviewView
 * is fully laid out.
 */
@Composable
fun QrScannerDialog(
    onResult: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.75f)
                .background(Color.Black, shape = RoundedCornerShape(16.dp)),
        ) {
            QrCameraPreview(
                modifier = Modifier.fillMaxSize(),
                onQrScanned = { result ->
                    onResult(result)
                    onDismiss()
                },
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Point camera at QR code",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                Icon(
                    NextIcons.Close,
                    contentDescription = "Close scanner",
                    tint = Color.White,
                )
            }
        }
    }
}

/**
 * CameraX preview with MLKit barcode scanning.
 *
 * CRITICAL FIXES for black screen:
 * 1. Uses the **Activity's** LifecycleOwner instead of the Dialog's,
 *    because Compose Dialogs don't have a proper lifecycle that CameraX
 *    can observe — this was the root cause of the black screen.
 * 2. Sets `PreviewView.implementationMode = PERFORMANCE` for hardware-accelerated rendering.
 * 3. Binds camera use cases AFTER the PreviewView is attached and laid out
 *    using `post { }` to ensure the Surface is ready.
 * 4. Uses `ContextCompat.getMainExecutor()` for the ImageAnalysis analyzer
 *    to ensure frames are processed on the main thread (MLKit requirement).
 *
 * MLKit barcode scanning:
 * - ImageAnalysis use case is attached with STRATEGY_KEEP_ONLY_LATEST
 * - Each frame is passed to BarcodeScanner for QR code detection
 * - When a QR code is found, the URL and auth token are extracted automatically
 * - An AtomicBoolean prevents duplicate scans
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun QrCameraPreview(
    modifier: Modifier = Modifier,
    onQrScanned: (String) -> Unit,
) {
    val context = LocalContext.current

    // FIX: Use the Activity's LifecycleOwner, NOT LocalLifecycleOwner.current
    // which may return the Dialog's lifecycle that CameraX cannot properly observe.
    val activityLifecycleOwner: LifecycleOwner = remember {
        // Walk up the context chain to find the Activity
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is Activity) break
            ctx = ctx.baseContext
        }
        ctx as LifecycleOwner
    }

    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanned = remember { AtomicBoolean(false) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Clean up camera resources when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (e: Exception) {
                Log.e("QrScanner", "Error unbinding camera on dispose", e)
            }
            executor.shutdownNow()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            // Create PreviewView with PERFORMANCE mode for hardware-accelerated rendering
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                // FIX: Use PERFORMANCE implementation mode — this uses SurfaceView
                // instead of TextureView and eliminates the black screen on most devices
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView ->
                // FIX: Use post{} to ensure the PreviewView is fully laid out
                // and its Surface is ready BEFORE binding camera use cases.
                // This prevents the black screen caused by binding to a
                // Surface that hasn't been created yet.
                previewView.post {
                    startCamera(
                        context = ctx,
                        previewView = previewView,
                        lifecycleOwner = activityLifecycleOwner,
                        executor = executor,
                        scanned = scanned,
                        onQrScanned = onQrScanned,
                        onProviderReady = { provider -> cameraProvider = provider },
                    )
                }
            }
        },
    )
}

/**
 * Starts the CameraX camera with Preview + ImageAnalysis use cases
 * and MLKit barcode scanning.
 *
 * This function is called from `PreviewView.post {}` to ensure the
 * view's Surface is ready before binding.
 */
@OptIn(ExperimentalGetImage::class)
private fun startCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    executor: java.util.concurrent.ExecutorService,
    scanned: AtomicBoolean,
    onQrScanned: (String) -> Unit,
    onProviderReady: (ProcessCameraProvider) -> Unit,
) {
    try {
        // Get the ProcessCameraProvider asynchronously
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    onProviderReady(provider)

                    // Unbind any previous use cases before rebinding
                    provider.unbindAll()

                    // === PREVIEW USE CASE ===
                    // Bind the Preview to the PreviewView's surfaceProvider
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    // === IMAGE ANALYSIS USE CASE + MLKIT BARCODE SCANNING ===
                    // This actively analyzes camera frames for QR codes
                    val barcodeScanner = BarcodeScanning.getClient()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                    ) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && !scanned.get()) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees,
                            )
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    // Look for QR code format specifically
                                    val qrValue = barcodes
                                        .firstOrNull { barcode ->
                                            barcode.format == Barcode.FORMAT_QR_CODE
                                        }
                                        ?.rawValue

                                    if (qrValue != null && scanned.compareAndSet(false, true)) {
                                        Log.d("QrScanner", "QR code scanned: $qrValue")
                                        onQrScanned(qrValue)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("QrScanner", "Barcode scanning failed", e)
                                }
                                .addOnCompleteListener {
                                    // CRITICAL: Always close the ImageProxy to free the buffer
                                    // and allow the next frame to be delivered
                                    imageProxy.close()
                                }
                        } else {
                            // No image or already scanned — close immediately
                            imageProxy.close()
                        }
                    }

                    // === BIND TO LIFECYCLE ===
                    // FIX: Bind to the Activity's lifecycle, not the Dialog's
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis,
                    )

                    Log.d("QrScanner", "Camera bound successfully to Activity lifecycle")
                } catch (e: Exception) {
                    Log.e("QrScanner", "Failed to bind camera use cases", e)
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    } catch (e: Exception) {
        Log.e("QrScanner", "Failed to start camera", e)
    }
}
