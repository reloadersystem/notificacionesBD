package correa.resembrink.dev.notificationsbd;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private CircleImageView mImageBtn;
    private EditText mEmailField;
    private EditText mNameField;
    private EditText mPasswordField;
    private Button mRegBtn;
    private Button mLoginPageBtn;

    private Uri imageUri;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private ProgressBar mRegisterProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageUri=null;

        mStorage= FirebaseStorage.getInstance().getReference().child("images");

        mAuth= FirebaseAuth.getInstance();
        mFirestore= FirebaseFirestore.getInstance();

        mImageBtn= findViewById(R.id.register_imagen_btn);
        mEmailField=findViewById(R.id.register_email);
        mPasswordField= findViewById(R.id.register_password);
        mNameField=findViewById(R.id.register_name);
        mRegBtn= findViewById(R.id.register_btn);
        mLoginPageBtn=findViewById(R.id.register_login_btn);
        mRegisterProgressBar=findViewById(R.id.registerProgressBar);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageUri != null) // condicionando por si no  selecciono ni una imagen
                {
                    mRegisterProgressBar.setVisibility(View.VISIBLE);

                    final String name = mNameField.getText().toString();
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    final String user_id = mAuth.getCurrentUser().getUid();

                                    StorageReference user_profile = mStorage.child(user_id + ".png");

                                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> uploadtask) {

                                            if (uploadtask.isSuccessful()) {

                                                final String download_url = uploadtask.getResult().getDownloadUrl().toString();

                                                String token_id = FirebaseInstanceId.getInstance().getToken();


                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("name", name);
                                                userMap.put("image", download_url);
                                                userMap.put("token_id", token_id);


                                                mFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mRegisterProgressBar.setVisibility(View.INVISIBLE);
                                                        sendToMain();

                                                    }

                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterActivity.this, "Error: " + uploadtask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        mRegisterProgressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });


                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                mRegisterProgressBar.setVisibility(View.INVISIBLE);
                                            }

                                        }
                                    });
                                }
                            }
                        });
                    }
                }

            }




        });


        mLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);

            }
        });
    }

    private void sendToMain() {

        Intent  mainIntent= new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE)
        {

            //elegir  foto de  Galeria del Mobile

            imageUri=data.getData();
            mImageBtn.setImageURI(imageUri);

        }
    }
}
