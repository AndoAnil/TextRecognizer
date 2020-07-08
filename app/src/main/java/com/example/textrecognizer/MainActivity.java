package com.example.textrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button choose;
    private TextView imageText;
    private ImageView imageView;
    public static final int PIC_IMAGE=121;
    private ImageButton camera;
    private TextView scanText;

    private static final int REQUEST_IMAGE_CAPTURE=111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choose=(Button)findViewById(R.id.choose);
        imageText=(TextView)findViewById(R.id.text);
        imageView=(ImageView)findViewById(R.id.image);
        camera=(ImageButton)findViewById(R.id.takeImageCamera);
        scanText=(TextView)findViewById(R.id.scanText);

        imageText.setMovementMethod(new ScrollingMovementMethod());

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });




        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Selcet Image"),PIC_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PIC_IMAGE)
        {
            imageView.setImageURI(data.getData());
            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                imageText.setText(" ");
                                imageText.setText(result.getText());
                                //processFirebaseTextReconnozationnResult(result);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Toast.makeText(MainActivity.this, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();
                                    }
                                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(requestCode==REQUEST_IMAGE_CAPTURE)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            FirebaseVisionImage image;
            try {
                image=FirebaseVisionImage.fromBitmap(imageBitmap);
                FirebaseVisionTextRecognizer textRecognizer=FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        imageText.setText(" ");
                        imageText.setText(firebaseVisionText.getText());
                        //processFirebaseTextReconnozationnResult(firebaseVisionText);
                    }
                }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

  /*  private void processFirebaseTextReconnozationnResult(FirebaseVisionText texts)
    {
        List<FirebaseVisionText.TextBlock> blocks=texts.getTextBlocks();
        if(blocks.size()==0)
        {
            Toast.makeText(this, "Text Not found", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0;i<blocks.size();i++)
        {
            List<FirebaseVisionText.Line> lines=blocks.get(i).getLines();
            for(int j=0;j<lines.size();j++)
            {
                List<FirebaseVisionText.Element> elements=lines.get(j).getElements();
                for(int k=0;k<elements.size();k++)
                {
                    Graphic textGraphic=new TextGraphic(mGraphicOverlay,elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                }
            }
        }

    }

   */

}