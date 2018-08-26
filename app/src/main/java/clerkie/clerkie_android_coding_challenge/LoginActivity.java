package clerkie.clerkie_android_coding_challenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
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
import java.util.Collection;

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

    // Register box UI references
    private View mRegisterForm;
    private ImageView mRegisterUserImage;
    private TextInputLayout mRegisterUserInput;
    private EditText mRegisterUser;
    private ImageView mRegisterPasswordImage;
    private TextInputLayout mRegisterPasswordInput;
    private EditText mRegisterPassword;
    private Button mRegisterButton;

    private View mProgressView;

    private View mTransparentLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Fields
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
//        mPasswordCheckField = (EditText) findViewById(R.id.password_check);


        // Click listeners
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.reveal_circle).setOnClickListener(this);

        // Set register UI views
        mRegisterForm = findViewById(R.id.include_register_form);
        mRegisterUserImage = findViewById(R.id.register_username_image);
        mRegisterUserInput = findViewById(R.id.register_username_input);
        mRegisterUser = findViewById(R.id.register_username);
        mRegisterPasswordImage = findViewById(R.id.register_password_image2);
        mRegisterPasswordInput = findViewById(R.id.register_password_input2);
        mRegisterPassword = findViewById(R.id.register_password2);
        mRegisterButton = findViewById(R.id.register_button);


        // Views
        mProgressView = findViewById(R.id.login_progress);
        mRevealCircle = findViewById(R.id.reveal_circle);
        mXinCircle = findViewById(R.id.x_in_circle);
        mTransparentLinearLayout = findViewById(R.id.include_login_form);
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

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.button_sign_in) {
        } else if (i == R.id.register_button) {
//            createNewAccount();
            for(ObjectAnimator o : reverseTest) o.reverse();
        } else if (i == R.id.reveal_circle) {
            if(mRegisterForm.getVisibility() == View.INVISIBLE) {
                animations = new ArrayList<>();
                runForwardAnimations();
            }
            else ;

        }
    }

    private ArrayList<ObjectAnimator> animations;
//
//    private ArrayList<ObjectAnimator> getCircleAnimations(){
//
//    }

    private void runForwardAnimations(){
        ObjectAnimator animationCircleX = ObjectAnimator.ofFloat(mRevealCircle, "x",
                mTransparentLinearLayout.getX()+(mTransparentLinearLayout.getWidth()-mRevealCircle.getWidth())/2);
        animationCircleX.setDuration(500);

        ObjectAnimator animationCircleY = ObjectAnimator.ofFloat(mRevealCircle, "y",
                mTransparentLinearLayout.getY()+(mTransparentLinearLayout.getHeight()-mRevealCircle.getHeight())/2);
        animationCircleY.setDuration(500);

        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x",
                mTransparentLinearLayout.getX()+(mTransparentLinearLayout.getWidth()-mXinCircle.getMeasuredWidth())/2);
        animationXinCircleX.setDuration(500);

        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y",
                mTransparentLinearLayout.getY()+(mTransparentLinearLayout.getHeight()-mXinCircle.getMeasuredHeight())/2);
        animationXinCircleY.setDuration(500);




        ObjectAnimator animationY = ObjectAnimator.ofFloat(mTransparentLinearLayout, "y",
                mTransparentLinearLayout.getY()-35);
        animationY.setDuration(1000);

        ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(mTransparentLinearLayout, "alpha",
                mTransparentLinearLayout.getAlpha()/2);
        animationAlpha.setDuration(1000);

        ObjectAnimator animationScaleX = ObjectAnimator.ofFloat(mTransparentLinearLayout, "scaleX", 0.95f);
        animationScaleX.setDuration(1000);

        ObjectAnimator animationScaleY = ObjectAnimator.ofFloat(mTransparentLinearLayout, "scaleY", 0.95f);
        animationScaleY.setDuration(1000);

        animationCircleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRevealCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final float returnX = mXinCircle.getX();
        final float returnY = mXinCircle.getY();

        animationCircleX.start();
        animationCircleY.start();
        animationXinCircleX.start();
        animationXinCircleY.start();

        animationY.start();
        animationAlpha.start();
        animationScaleX.start();
        animationScaleY.start();

        displayRegisterForm(returnX, returnY);
    }


    private void displayRegisterForm(final float x, final float y){
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
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animationXinCircleForward(x, y);
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


    private ArrayList<ObjectAnimator> reverseTest = new ArrayList<ObjectAnimator>();

    private void animationXinCircleForward(float x, float y){
        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x", x);
        animationXinCircleX.setDuration(500);

        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y", y);
        animationXinCircleY.setDuration(500);

        ObjectAnimator animationXinCircleRotation = ObjectAnimator.ofFloat(mXinCircle, "rotation", 45);
        animationXinCircleRotation.setDuration(500);

        reverseTest.add(animationXinCircleX);
        reverseTest.add(animationXinCircleY);
        reverseTest.add(animationXinCircleRotation);

        animationXinCircleX.start();
        animationXinCircleY.start();
        animationXinCircleRotation.start();

//        animationXinCircleX.reverse();
    }
}

