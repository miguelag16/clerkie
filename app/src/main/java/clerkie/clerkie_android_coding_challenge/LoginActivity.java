package clerkie.clerkie_android_coding_challenge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent.getData() == null) return;
        String emailLink = intent.getData().toString();

// Confirm the link is a sign-in with email link.
        if (mAuth.isSignInWithEmailLink(emailLink)) {
            String email; // retrieve this from wherever you stored it
            // The client SDK will parse the code from the link for you.
            mAuth.signInWithEmailLink("miguel16@stanford.edu", emailLink)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully signed in with email link!");
                                AuthResult result = (AuthResult) task.getResult();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                                Toast.makeText(LoginActivity.this, "Successfully signed in with email link!",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Log.e(TAG, "Error signing in with email link: "
                                        + task.getException().getMessage());
                                Toast.makeText(LoginActivity.this, "Error signing in with email link: "
                                                + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
//        updateUI(currentUser);
    }

//    // [START create_user_with_email]
//        mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                // Sign in success, update UI with the signed-in user's information
//                Log.d(TAG, "createUserWithEmail:success");
//                FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//            } else {
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                Toast.makeText(LoginActivity.this, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//            }
//
//            // [START_EXCLUDE]
////                        hideProgressDialog();
//            // [END_EXCLUDE]
//        }
//    });
//    // [END create_user_with_email]






    // UI references.
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordCheckField;
    private View mRevealCircle;
    private TextView mXinCircle;

    // Login box UI references
    private View mLoginForm;
    private View mLoginLayout;
    private Button mLoginButton;
    private View mLoginSwitch;
    private ImageView mLoginUserImage;
    private TextInputLayout mLoginUserInput;
    private EditText mLoginUser;
    private ImageView mLoginPasswordImage;
    private TextInputLayout mLoginPasswordInput;
    private EditText mLoginPassword;
    private TextView mLoginForgotPassword;

    // Register box UI references
    private View mRegisterForm;
    private View mRegisterLayout;
    private Button mRegisterButton;
    private View mRegisterSwitch;
    private ImageView mRegisterUserImage;
    private TextInputLayout mRegisterUserInput;
    private EditText mRegisterUser;
    private ImageView mRegisterPasswordImage;
    private TextInputLayout mRegisterPasswordInput;
    private EditText mRegisterPassword;
    private ImageView mRegisterPasswordImage2;
    private TextInputLayout mRegisterPasswordInput2;
    private EditText mRegisterPassword2;

    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Fields
        mEmailField = (EditText) findViewById(R.id.login_username);
        mPasswordField = (EditText) findViewById(R.id.login_password);
//        mPasswordCheckField = (EditText) findViewById(R.id.password_check);


        // Click listeners
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.reveal_circle).setOnClickListener(this);
        findViewById(R.id.x_in_circle).setOnClickListener(this);

        // Set login UI views
        mLoginForm = findViewById(R.id.include_login_form);
        mLoginLayout = findViewById(R.id.login_layout_root);
        mLoginButton = findViewById(R.id.login_button);
        mLoginSwitch = findViewById(R.id.login_switch);
        mLoginUserImage = findViewById(R.id.login_username_image);
        mLoginUserInput = findViewById(R.id.login_username_input);
        mLoginUser = findViewById(R.id.login_username);
        mLoginPasswordImage = findViewById(R.id.login_password_image);
        mLoginPasswordInput = findViewById(R.id.login_password_input);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginForgotPassword = findViewById(R.id.login_forgot_password);

        // Set register UI views
        mRegisterForm = findViewById(R.id.include_register_form);
        mRegisterLayout = findViewById(R.id.register_layout_root);
        mRegisterButton = findViewById(R.id.register_button);
        mRegisterSwitch = findViewById(R.id.register_switch);
        mRegisterUserImage = findViewById(R.id.register_username_image);
        mRegisterUserInput = findViewById(R.id.register_username_input);
        mRegisterUser = findViewById(R.id.register_username);
        mRegisterPasswordImage = findViewById(R.id.register_password_image);
        mRegisterPasswordInput = findViewById(R.id.register_password_input);
        mRegisterPassword = findViewById(R.id.register_password);
        mRegisterPasswordImage2 = findViewById(R.id.register_password_image2);
        mRegisterPasswordInput2 = findViewById(R.id.register_password_input2);
        mRegisterPassword2 = findViewById(R.id.register_password2);

        // Views
        mProgressView = findViewById(R.id.login_progress);
        mRevealCircle = findViewById(R.id.reveal_circle);
        mXinCircle = findViewById(R.id.x_in_circle);
    }

    private void createNewAccount() {
        Log.d(TAG, "createAccount:" + mEmailField.getText().toString());
        if (!validateForm()) {
            return;
        }

        verifyNewUser();
//        showProgressDialog();
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        // Handle passwords not being the same while registering process

        return valid;
    }

    private void verifyNewUser() {
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://clerkie-coding-challenge.firebaseapp.com")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "clerkie.clerkie_android_coding_challenge",
                                true, /* installIfNotAvailable */
                                "1"    /* minimumVersion */)
                        .build();

        mAuth.sendSignInLinkToEmail("miguel16@stanford.edu", actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Verification mail sent successfully.");
                        } else {
                            Log.w(TAG, "Verification mail failed to send.");
                        }
                    }
                });
    }


    private boolean reveal_forward;
    private ArrayList<ObjectAnimator> buttonToCornerAnimations = new ArrayList<>();
    private ArrayList<ObjectAnimator> buttonToCenterAnimations = new ArrayList<>();
    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.login_button) {
        } else if (i == R.id.register_button) {
//            createNewAccount();
        } else if (i == R.id.x_in_circle) {

            if(mRegisterForm.getVisibility() == View.INVISIBLE) {
                reveal_forward = true;
                buttonToCornerAnimations.clear();
                buttonToCenterAnimations.clear();
                runForwardAnimations();
            }
            else {
                reveal_forward = false;
                runBackwardAnimations();
            }

        }
    }

    private void runForwardAnimations(){
        ArrayList<ObjectAnimator> circleToCenterAnimations = getCircleToCenterAnimations();
        ArrayList<ObjectAnimator> loginToBackAnimations = getLoginToBackAnimations();

        final float cornerX = mXinCircle.getX();
        final float cornerY = mXinCircle.getY();

        buttonToCenterAnimations.addAll(circleToCenterAnimations);
        buttonToCenterAnimations.addAll(loginToBackAnimations);

        for(ObjectAnimator objectAnimator: buttonToCenterAnimations) objectAnimator.start();

        ArrayList<ObjectAnimator> xToCornerAnimations = getXtoCornerAnimations(cornerX, cornerY);
        buttonToCornerAnimations.addAll(xToCornerAnimations);

        revealForward(xToCornerAnimations);
    }

    private void runBackwardAnimations(){
        for(ObjectAnimator objectAnimator : buttonToCornerAnimations) objectAnimator.reverse();
        revealBackward();
    }

    private ArrayList<ObjectAnimator> getCircleToCenterAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationCircleX = ObjectAnimator.ofFloat(mRevealCircle, "x",
                mLoginForm.getX()+(mLoginForm.getWidth()-mRevealCircle.getWidth())/2);
        animationCircleX.setDuration(500);

        animationCircleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(reveal_forward) mRevealCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator animationCircleY = ObjectAnimator.ofFloat(mRevealCircle, "y",
                mLoginForm.getY()+(mLoginForm.getHeight()-mRevealCircle.getHeight())/2);
        animationCircleY.setDuration(500);

//        ObjectAnimator animationCircleAlpha = ObjectAnimator.ofFloat(mRevealCircle, "alpha", 0);
//        animationCircleAlpha.setDuration(500);

        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x",
                mLoginForm.getX()+(mLoginForm.getWidth()-mXinCircle.getMeasuredWidth())/2);
        animationXinCircleX.setDuration(500);

        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y",
                mLoginForm.getY()+(mLoginForm.getHeight()-mXinCircle.getMeasuredHeight())/2);
        animationXinCircleY.setDuration(500);

        arr.add(animationCircleX);
        arr.add(animationCircleY);
//        arr.add(animationCircleAlpha);
        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);

        return arr;
    }

    private ArrayList<ObjectAnimator> getLoginToBackAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationY = ObjectAnimator.ofFloat(mLoginForm, "y",
                mLoginForm.getY()-35);
        animationY.setDuration(1000);

        ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(mLoginForm, "alpha",
                mLoginForm.getAlpha()/2);
        animationAlpha.setDuration(1000);

        ObjectAnimator animationScaleX = ObjectAnimator.ofFloat(mLoginForm, "scaleX", 0.95f);
        animationScaleX.setDuration(1000);

        ObjectAnimator animationScaleY = ObjectAnimator.ofFloat(mLoginForm, "scaleY", 0.95f);
        animationScaleY.setDuration(1000);

        arr.add(animationY);
        arr.add(animationAlpha);
        arr.add(animationScaleX);
        arr.add(animationScaleY);

        return arr;
    }

    private ArrayList<ObjectAnimator> getXtoCornerAnimations(float x, float y){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x", x);
        animationXinCircleX.setDuration(500);

        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y", y);
        animationXinCircleY.setDuration(500);

        ObjectAnimator animationXinCircleRotation = ObjectAnimator.ofFloat(mXinCircle, "rotation", 45);
        animationXinCircleRotation.setDuration(500);

        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);
        arr.add(animationXinCircleRotation);

        return arr;
    }

    private void revealForward(final ArrayList<ObjectAnimator> xToCornerAnimations){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                    mRegisterForm,
                    mRegisterForm.getWidth()/2,
                    mRegisterForm.getHeight()/2,
                    mRevealCircle.getWidth()/2,
                    mRegisterForm.getWidth()/2);

            revealAnimator.setStartDelay(500);
            revealAnimator.setDuration(1000);

            revealAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mRegisterForm.setVisibility(View.VISIBLE);
                    setRegisterFieldVisibilities(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterButton.setVisibility(View.VISIBLE);
                    setRegisterFieldVisibilities(true);
                    for(ObjectAnimator objectAnimator : xToCornerAnimations) objectAnimator.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            revealAnimator.start();
    }

    private void revealBackward(){
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                mRegisterForm,
                mRegisterForm.getWidth()/2,
                mRegisterForm.getHeight()/2,
                mRegisterForm.getWidth()/2,
                mRevealCircle.getWidth()/2);

        revealAnimator.setStartDelay(500);
        revealAnimator.setDuration(1000);

        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setRegisterFieldVisibilities(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterForm.setVisibility(View.INVISIBLE);
                for(ObjectAnimator objectAnimator : buttonToCenterAnimations) objectAnimator.reverse();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        revealAnimator.start();
    }

    private void setRegisterFieldVisibilities(boolean bool){
        mRegisterButton.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterSwitch.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterUserImage.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterUserInput.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterUser.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordImage.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordInput.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPassword.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordImage2.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordInput2.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
        mRegisterPassword2.setVisibility(bool ? View.VISIBLE: View.INVISIBLE);
    }
}

