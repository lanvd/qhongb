package com.example.qhongb;

 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

 
import blade.kit.DateKit;
import blade.kit.http.HttpRequest;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ImageView;

public class Qr extends Activity {
	ImageView imageView;
	
	
	public Bitmap showQrCode(String uuid) {

		String url = "https://login.weixin.qq.com/qrcode/" + uuid;

		final File output = new File("temp.jpg");

		HttpRequest.post(url, true, "t", "webwx", "_",
				DateKit.getCurrentUnixTime()).receive(output);
		try {
			FileInputStream fis = new FileInputStream(output);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
 
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrview);
		//imageView = (ImageView) findViewById(R.id.imageView1);
		Bundle bundle = this.getIntent().getExtras();
		String uuid = bundle.getString("UUID");
		WebView wb = (WebView) findViewById(R.id.webView1);
		
		if (uuid != null) {
			//  Bitmap bitmap = showQrCode(uuid);
			if (wb !=null) {
				wb.loadUrl("https://login.weixin.qq.com/qrcode/"+uuid);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.qr, menu);
		return true;
	}

}
