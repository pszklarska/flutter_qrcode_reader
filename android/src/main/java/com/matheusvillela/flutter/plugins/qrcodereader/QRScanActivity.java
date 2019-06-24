// Copyright (c) <2017> <Matheus Villela>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.matheusvillela.flutter.plugins.qrcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class QRScanActivity extends Activity implements BarcodeCallback {

    private boolean qrRead;
    private CompoundBarcodeView view;

    public static String EXTRA_RESULT = "extra_result";

    public static String EXTRA_FOCUS_INTERVAL = "extra_focus_interval";
    public static String EXTRA_FORCE_FOCUS = "extra_force_focus";
    public static String EXTRA_TORCH_ENABLED = "extra_torch_enabled";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_read);

        setupCameraView();
        setupCancelButton();

    }

    private void setupCameraView() {
        view = findViewById(R.id.activity_qr_read_reader);
        Intent intent = getIntent();
        view.decodeContinuous(this);
        view.setStatusText("");
        if (intent.getBooleanExtra(EXTRA_TORCH_ENABLED, false)) {
            view.setTorchOn();
        } else {
            view.setTorchOff();
        }
    }

    private void setupCancelButton() {
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    public void barcodeResult(BarcodeResult result) {
        if (!qrRead) {
            synchronized (this) {
                qrRead = true;
                Intent data = new Intent();
                data.putExtra(EXTRA_RESULT, result.getText());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

    }
}