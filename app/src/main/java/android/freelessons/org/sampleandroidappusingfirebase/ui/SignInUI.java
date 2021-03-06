package android.freelessons.org.sampleandroidappusingfirebase.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.freelessons.org.sampleandroidappusingfirebase.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInUI extends DialogFragment {
    Button signInButton;
    EditText passwordEditText;
    AutoCompleteTextView emailAutoCompleteTextView;
    FirebaseAuth firebaseAuth;
    public static SignInUI newInstance(){
        return new SignInUI();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signin, null, false);
        findViews(v);
        firebaseAuth = FirebaseAuth.getInstance();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    private void findViews(View rootView){
        signInButton=(Button)rootView.findViewById(R.id.email_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        emailAutoCompleteTextView=(AutoCompleteTextView)rootView.findViewById(R.id.email);
        passwordEditText=(EditText)rootView.findViewById(R.id.password);
    }
    private void signIn(){
        if(inputValid()) {
            firebaseAuth.signInWithEmailAndPassword(emailAutoCompleteTextView.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity().getApplicationContext(), "Sign in successful", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Error: ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            if(!isEmailValid()){
                Toast.makeText(getContext(), "Entered email is invalid", Toast.LENGTH_SHORT).show();
            }else if(isEmailValid()){
                Toast.makeText(getContext(), "Entered password is invalid", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private boolean inputValid(){
        return isEmailValid()&&isPasswordValid();
    }
    private boolean isEmailValid(){
        return !emailAutoCompleteTextView.getText().toString().isEmpty();
                //&& emailAutoCompleteTextView.getText().toString();
    }
    private boolean  isPasswordValid(){
        return !passwordEditText.getText().toString().isEmpty() && passwordEditText.getText().toString().length()>6;
    }

}
