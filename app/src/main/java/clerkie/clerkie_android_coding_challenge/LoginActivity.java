package clerkie.clerkie_android_coding_challenge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabaseReference;

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

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            String email = sharedPref.getString(mNewUserEmailKey, "");
            final String password = sharedPref.getString(mNewUserPasswordKey, "");

            // The client SDK will parse the code from the link for you.
            mAuth.signInWithEmailLink(email, emailLink)
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

                                FirebaseUser user = result.getUser();
                                user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                        }
                                    }
                                });
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

    // Shared preferences keys
    String mVerificationIdKey = "verification_id";
    String mNewUserEmailKey = "new_user_username";
    String mNewUserPasswordKey = "new_user_password";

    // UI references.
    private TextView mLoginActivityMessage;
    private View mRevealCircle;
    private TextView mXinCircle;
    private Switch mSwitch;

    // Login box UI references
    private View mLoginForm;
    private View mLoginLayout;
    private Button mLoginButton;
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
    private TextView mRegisterMessageBox;
    private Button mRegisterButton;
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

        // Set login UI views
        mLoginForm = findViewById(R.id.include_login_form);
        mLoginLayout = findViewById(R.id.login_layout_root);
        mLoginButton = findViewById(R.id.login_button);
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
        mRegisterMessageBox = findViewById(R.id.register_message_box);
        mRegisterButton = findViewById(R.id.register_button);
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
        mLoginActivityMessage = findViewById(R.id.login_activity_message);
        mRevealCircle = findViewById(R.id.reveal_circle);
        mXinCircle = findViewById(R.id.x_in_circle);
        mSwitch = findViewById(R.id.switch1);

        // Listeners
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mRevealCircle.setOnClickListener(this);
        mXinCircle.setOnClickListener(this);
        TextView.OnEditorActionListener sendSMSCodeOnEnterPressed = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mSwitch.isChecked()) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        sendSMSCode();
                    }
                }
                return false;
            }
        };
        mLoginUser.setOnEditorActionListener(sendSMSCodeOnEnterPressed);


        showInstructions(R.string.login_instructions_initial);
    }


    private void login(){
        if (!validateLoginForm()) {
            return;
        }

        if (mSwitch.isChecked()){
            String code = mLoginPassword.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mInstanceStateVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            signInWithEmailAndPassword();
        }
    }

    private void registerNewEmailUser() {
        Log.d(TAG, "createAccount:" + mRegisterUser.getText().toString());
        if (!validateRegisterForm()) {
            return;
        }
        createNewEmailUsernameAccount();
    }

    private void signInWithEmailAndPassword(){
        mAuth.signInWithEmailAndPassword(mLoginUser.getText().toString(), mLoginPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. " + mLoginUser.getText().toString() + " " + mLoginPassword.getText().toString(),
                                    Toast.LENGTH_SHORT).show();

                        }
                        // ...
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showInstructions(R.string.sms_incorrect);
                            }
                        }
                    }
                });
    }


    private boolean validateLoginForm(){
        boolean valid = true;

        if (TextUtils.isEmpty(mLoginUser.getText().toString())) {
            mLoginUser.setError("Required.");
            valid = false;
        } else {
            mLoginUser.setError(null);
        }

        if (TextUtils.isEmpty(mLoginPassword.getText().toString())) {
            mLoginPassword.setError("Required.");
            valid = false;
        } else {
            mLoginPassword.setError(null);
        }

        return valid;
    }


    private boolean validateRegisterForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mRegisterUser.getText().toString())) {
            mRegisterUser.setError("Required.");
            valid = false;
        } else {
            mRegisterUser.setError(null);
        }

        if (TextUtils.isEmpty(mRegisterPassword.getText().toString())) {
            mRegisterPassword.setError("Required.");
            valid = false;
        } else {
            mRegisterPassword.setError(null);
        }

        if (TextUtils.isEmpty(mRegisterPassword2.getText().toString())) {
            mRegisterPassword2.setError("Required.");
            valid = false;
        } else {
            mRegisterPassword2.setError(null);
        }

        // Handle passwords not being the same while registering process
        if (!mRegisterPassword.getText().toString().equals(mRegisterPassword2.getText().toString())) {
            mRegisterPassword2.setError("Passwords must match.");
            valid = false;
        } else {
            mRegisterPassword2.setError(null);
        }

        return valid;
    }

    private void createNewEmailUsernameAccount(){
        mAuth.createUserWithEmailAndPassword(mRegisterUser.getText().toString(), mRegisterPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: success");
                            sendVerificationEmailToUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail: failure", task.getException());
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                mRegisterMessageBox.setText(R.string.registration_failure_weak_password);
                                showMessageInRegistrationBox();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mRegisterMessageBox.setText(R.string.registration_failure_invalid_credentials);
                                showMessageInRegistrationBox();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mRegisterMessageBox.setText(R.string.registration_failure_account_collision);
                                showMessageInRegistrationBox();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }


    private void sendVerificationEmailToUser() {
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

        mAuth.sendSignInLinkToEmail(mRegisterUser.getText().toString(), actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        onVerificationEmailSendAttempt(task.isSuccessful());
                    }
                });
    }


    private void onVerificationEmailSendAttempt(boolean sent){
        if (sent){
            String user = mRegisterUser.getText().toString();
            String password = mRegisterPassword.getText().toString();
            Toast.makeText(this, user + " " + password, Toast.LENGTH_LONG).show();

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(mNewUserEmailKey, user);
            editor.putString(mNewUserPasswordKey, password);
            editor.commit();

            Log.d(TAG, "Verification mail sent successfully.");
            mRegisterMessageBox.setText(R.string.registration_email_success);
        } else {

            Log.w(TAG, "Verification mail failed to send.");
            mRegisterMessageBox.setText(R.string.registration_email_failure);
        }

        showMessageInRegistrationBox();
    }


    private String mInstanceStateVerificationId;
    private void sendSMSCode() {
        if (mLoginUser.getText() == null || mLoginUser.getText().toString().length() != 10) {
            showInstructions(R.string.sms_malformed_number1);
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+1" + mLoginUser.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        Log.d(TAG, "onVerificationCompleted:" + credential);

                        showInstructions(R.string.sms_sent_and_autoverified);
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);

                        showInstructions(R.string.sms_sent_failed);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId);

                        showInstructions(R.string.sms_sent_success);

                        // Save verification ID and resending token so we can use them later
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(mVerificationIdKey, verificationId);
                        editor.commit();
//                        mResendToken = token;
                    }
                });

    }


    private void showMessageInRegistrationBox(){
        mRegisterMessageBox.setVisibility(View.VISIBLE);
        setRegisterFieldVisibilities(false);
        setLoginFieldVisibilities(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                mRegisterMessageBox.setVisibility(View.INVISIBLE);
                setLoginFieldVisibilities(true);
                runBackwardAnimations();
            }
        }, 10000);
    }


    private boolean reveal_forward;
    private ArrayList<ObjectAnimator> buttonToCornerAnimations = new ArrayList<>();
    private ArrayList<ObjectAnimator> buttonToCenterAnimations = new ArrayList<>();

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

        revealRegisterFormForward(xToCornerAnimations);
    }


    private void runBackwardAnimations(){
        // Send the 'X' to the center first by reversing these animations
        for(ObjectAnimator objectAnimator : buttonToCornerAnimations) objectAnimator.reverse();
        revealRegisterFormBackward();
    }


    private ArrayList<ObjectAnimator> getCircleToCenterAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationCircleX = ObjectAnimator.ofFloat(mRevealCircle, "x",
                mLoginForm.getX()+(mLoginForm.getWidth()-mRevealCircle.getWidth())/2);
        animationCircleX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

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
        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x",
                mLoginForm.getX()+(mLoginForm.getWidth()-mXinCircle.getMeasuredWidth())/2);
        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y",
                mLoginForm.getY()+(mLoginForm.getHeight()-mXinCircle.getMeasuredHeight())/2);

        arr.add(animationCircleX);
        arr.add(animationCircleY);
        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);

        return arr;
    }


    private ArrayList<ObjectAnimator> getLoginToBackAnimations(){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationY = ObjectAnimator.ofFloat(mLoginForm, "y",
                mLoginForm.getY()-35);
        ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(mLoginForm, "alpha",
                mLoginForm.getAlpha()/2);
        ObjectAnimator animationScaleX = ObjectAnimator.ofFloat(mLoginForm, "scaleX", 0.95f);
        ObjectAnimator animationScaleY = ObjectAnimator.ofFloat(mLoginForm, "scaleY", 0.95f);

        arr.add(animationY);
        arr.add(animationAlpha);
        arr.add(animationScaleX);
        arr.add(animationScaleY);

        return arr;
    }


    private ArrayList<ObjectAnimator> getXtoCornerAnimations(float x, float y){
        ArrayList<ObjectAnimator> arr = new ArrayList<>();

        ObjectAnimator animationXinCircleX = ObjectAnimator.ofFloat(mXinCircle, "x", x);
        ObjectAnimator animationXinCircleY = ObjectAnimator.ofFloat(mXinCircle, "y", y);
        ObjectAnimator animationXinCircleRotation = ObjectAnimator.ofFloat(mXinCircle, "rotation", 45);

        arr.add(animationXinCircleX);
        arr.add(animationXinCircleY);
        arr.add(animationXinCircleRotation);

        return arr;
    }


    private void revealRegisterFormForward(final ArrayList<ObjectAnimator> xToCornerAnimations){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                    mRegisterForm,
                    mRegisterForm.getWidth()/2,
                    mRegisterForm.getHeight()/2,
                    mRevealCircle.getWidth()/2,
                    mRegisterForm.getWidth()/2);

            revealAnimator.setStartDelay(250);

            revealAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(reveal_forward) mRevealCircle.setVisibility(View.INVISIBLE);
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


    private void revealRegisterFormBackward(){
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                mRegisterForm,
                mRegisterForm.getWidth()/2,
                mRegisterForm.getHeight()/2,
                mRegisterForm.getWidth()/2,
                mRevealCircle.getWidth()/2);

        revealAnimator.setStartDelay(250);

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


    private ObjectAnimator currentInstructionAnimation = null;
    private void showInstructions(final int message){
        if(currentInstructionAnimation != null) currentInstructionAnimation.end();

        final ObjectAnimator animationShow = ObjectAnimator.ofFloat(mLoginActivityMessage, "alpha", 1);
        animationShow.setStartDelay(1000);
        animationShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                currentInstructionAnimation = animationShow;
                mLoginActivityMessage.setText(message);
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        final ObjectAnimator animationHide = ObjectAnimator.ofFloat(mLoginActivityMessage, "alpha", 0);
                        animationHide.setDuration(500);
                        animationHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                currentInstructionAnimation = animationHide;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLoginActivityMessage.setText("");
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animationHide.start();
                    }
                }, 10000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationShow.start();
    }


    private void setRegisterFieldVisibilities(boolean visible){
        findViewById(R.id.register_static_instructions).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mRegisterButton.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterUserImage.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterUserInput.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterUser.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordImage.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordInput.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPassword.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordImage2.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPasswordInput2.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
        mRegisterPassword2.setVisibility(visible ? View.VISIBLE: View.INVISIBLE);
    }

    private void setLoginFieldVisibilities(boolean visible){
        mLoginButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginUserImage.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginUserInput.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginUser.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginPasswordImage.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginPasswordInput.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginPassword.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mLoginForgotPassword.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void displayRegisterForm(){
        reveal_forward = true;
        buttonToCornerAnimations.clear();
        buttonToCenterAnimations.clear();
        runForwardAnimations();
    }

    private void hideRegisterForm(){
        reveal_forward = false;
        runBackwardAnimations();
    }

    public void switchOnClick(View view){
        mLoginUser.setText("");
        if(mLoginUser.getInputType() != (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE)) {
            mLoginUser.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
            mLoginUser.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(10)
            });
            showInstructions(R.string.login_instructions_phone);
        } else {
            mLoginUser.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
            mLoginUser.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(50)
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.login_button) {
            login();
        } else if (i == R.id.register_button) {
            registerNewEmailUser();
        } else if (i == R.id.x_in_circle) {
            if (mRegisterForm.getVisibility() == View.INVISIBLE) {
                displayRegisterForm();
            } else {
                hideRegisterForm();
            }
        }
    }

}

