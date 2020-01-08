package org.com.example.dogal_sepeti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpWithEmailActivity extends AppCompatActivity {
    private static final int GalleryPick = 1;
    private HashMap<String, Object> profileMap;
    private Spinner provinceSpinner;
    private Spinner districtSpinner;
    private CircleImageView userProfileImage;
    private StorageReference userProfileImagesRef;
    private EditText name, surname, email, phone, password1, password2, dateOfBirth;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserID;
    private ProgressDialog progressDialog;
    private ImageButton backArrow_image_btn;
    private String userName;
    private String userSurname;
    private String userEmail;
    private String userPhone;
    private String userDateOfBirth;
    private String userPassword;
    private String userGender;
    private String userCity;
    private int resultCodeForImage;
    private String userDistrict;
    private ArrayList<String> cities_arrayList;
    private ArrayList<String> districts_arrayList;
    private CropImage.ActivityResult Image_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);

        initializeFields();
        setCityAndDistrictsSpinner();

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }


    private void createNewAccount() {
        userName = name.getText().toString();
        userSurname = surname.getText().toString();
        userEmail = email.getText().toString();
        userPhone = phone.getText().toString();
        userDateOfBirth = dateOfBirth.getText().toString();
        if (!arePasswordsSame()) {
            Toast.makeText(getApplicationContext(), "Passwords are not matching...", Toast.LENGTH_SHORT).show();
        } else {
            userPassword = password1.getText().toString();

            if (!isEmpty(userName, userSurname, userEmail, userPhone, userDateOfBirth, userPassword, userGender, userCity, userDistrict)) {
                progressDialog.setTitle("Creating New Account");
                progressDialog.setMessage("Please wait, while we are creating new account for you...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    currentUserID = mAuth.getCurrentUser().getUid();
                                    addUserInfoToHashMap();

                                    rootRef.child("Users").child(currentUserID).setValue("");
                                    rootRef.child("Users").child(currentUserID).child("device_token")
                                            .setValue(deviceToken);

                                    rootRef.child("Users").child(currentUserID).updateChildren(profileMap);
                                    continueImageProcess();
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(getApplicationContext(), "Error : " + message, Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }else{
                Toast.makeText(this, "Please fill the blanks.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean arePasswordsSame() {
        return password1.getText().toString().equals(password2.getText().toString());
    }

    private void addUserInfoToHashMap() {
        profileMap = new HashMap<>();
        profileMap.put("id", currentUserID);
        profileMap.put("name", userName);
        profileMap.put("surname", userSurname);
        profileMap.put("email", userEmail);
        profileMap.put("phone_num", userPhone);
        profileMap.put("dateOfBirth", userDateOfBirth);
        profileMap.put("gender", userGender);
        profileMap.put("city", userCity);
        profileMap.put("district", userDistrict);

    }


    private void activateBackArrowButton() {
        backArrow_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });
    }

    public boolean isEmpty(String userName, String userSurname, String userEmail, String userPhone, String userDateOfBirth, String userPassword, String userGender, String userCity, String userDistrict) {
        if (TextUtils.isEmpty(userName)) {
            //Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userSurname)) {
            //Toast.makeText(this, "Please enter your surname...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userEmail)) {
            //Toast.makeText(this, "Please enter your email...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userPhone)) {
            //Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userDateOfBirth)) {
            //Toast.makeText(this, "Please enter your date of birth...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userPassword)) {
            //Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userGender)) {
            //Toast.makeText(this, "Please enter your gender...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userCity)) {
            //Toast.makeText(this, "Please enter your city...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(userDistrict)) {
            //Toast.makeText(this, "Please enter your district...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    public void onRadioButtonClicked(View view) {
        userGender = "";
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.femaleRadioButton:
                if (checked)
                    userGender = "Kadın";
                break;
            case R.id.maleRadioButton:
                if (checked)
                    userGender = "Erkek";
                break;
        }
    }


    private void initializeFields() {
        backArrow_image_btn = findViewById(R.id.arrow);
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone_No);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        dateOfBirth = findViewById(R.id.dataOfBirth);

        CreateAccountButton = findViewById(R.id.signUpButton);
        userProfileImage = findViewById(R.id.set_profile_image);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        activateBackArrowButton();
    }

    public ArrayList<String> getCities(ArrayList<String> cities) {
        // Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        // Build Document
        Document document = null;
        try {

            InputStream istream = getAssets().open("data.xml");
            builder = factory.newDocumentBuilder();
            document = builder.parse(istream);
            // Normalize the XML Structure; It's just too important !!
            document.getDocumentElement().normalize();

            // Get all cities
            NodeList cList = document.getElementsByTagName("city");

            for (int i = 0; i < cList.getLength(); i++) {
                // taking just one city
                Node node = cList.item(i);
                //System.out.println(""); // Just a separator
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) node;


                    cities.add(eElement.getAttribute("cityName"));
                    // System.out.println("cityId : " + eElement.getAttribute("cityId"));
                    Log.i("cityName", eElement.getAttribute("cityName"));
                    System.out.println("cityName : " + eElement.getAttribute("cityName"));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return cities;
    }

    public void getDistrict(int position) {
        districts_arrayList = new ArrayList<>();
        if (position == 0) {
            districts_arrayList.add("ILÇE SEÇİNİZ");
        } else {
            districts_arrayList.add("ILÇE SEÇİNİZ");
            // Get Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            // Build Document
            Document document = null;
            try {
                InputStream istream = getAssets().open("data.xml");
                builder = factory.newDocumentBuilder();
                document = builder.parse(istream);
                // Normalize the XML Structure; It's just too important !!
                document.getDocumentElement().normalize();
                // Get all cities
                NodeList cList = document.getElementsByTagName("city");
                // taking just one city
                Node node = cList.item(position - 1);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    // Get all districts
                    NodeList dList = eElement.getElementsByTagName("district");
                    for (int j = 0; j < dList.getLength(); j++) {
                        // taking just one district
                        Node dNode = dList.item(j);
                        if (dNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element dElement = (Element) dNode;
                            districts_arrayList.add(dElement.getElementsByTagName("districtName").item(0).getTextContent());
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ArrayAdapter<String> adapter_districts = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districts_arrayList);
        districtSpinner.setAdapter(adapter_districts);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    userDistrict = districts_arrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setCityAndDistrictsSpinner() {
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);

        cities_arrayList = new ArrayList<>();
        cities_arrayList.add("ŞEHİR SEÇİNİZ");
        cities_arrayList = getCities(cities_arrayList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities_arrayList);
        provinceSpinner.setAdapter(adapter);

/***  *********************************************************************   ***/
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    userCity = cities_arrayList.get(position);
                }
                getDistrict(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Image_result = CropImage.getActivityResult(data);
            resultCodeForImage = resultCode;
            Toast.makeText(this, "Your Image attached successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void continueImageProcess() {
        if (resultCodeForImage == RESULT_OK) {

            progressDialog.setTitle("Set Profile Image");
            progressDialog.setMessage("Please wait, your profile image is uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Uri resultUri = Image_result.getUri();


            StorageReference filePath = userProfileImagesRef.child(currentUserID + ".jpg");

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                        final String downloadedUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                        rootRef.child("Users").child(currentUserID).child("image")
                                .setValue(downloadedUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Image saved in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }


}

class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
}

