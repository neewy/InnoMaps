package com.innopolis.maps.innomaps.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Auth extends Fragment {

    private static final String TAG = "Authentication";

    @InjectView(R.id.input_auth_code)
    EditText editText;
    @InjectView(R.id.btn_confirm)
    Button confirmButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth, container, false);

        ButterKnife.inject(this, view);

        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        return view;
    }


    public void login() {
        Log.d(TAG, getContext().getString(R.string.auth));

        if (!validate()) {
            onConfirmFailed();
            return;
        }

        confirmButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getContext().getString(R.string.confirming));
        progressDialog.show();

        String auth_code = editText.getText().toString();

        // TODO: Implement authentication logic

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onConfirmSuccess or onConfirmFailed
                        onConfirmSuccess();
                        // onConfirmFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }



/*    public void onBackPressed() {
        //TODO: return to login/sign up
    }*/

    public void onConfirmSuccess() {
        confirmButton.setEnabled(true);
    }

    public void onConfirmFailed() {
        Toast.makeText(getContext(), R.string.login_failed, Toast.LENGTH_LONG).show();

        confirmButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String auth_code = editText.getText().toString();

        if (auth_code.isEmpty() || auth_code.length() != 6) {
            editText.setError(getContext().getString(R.string.be_6_numbers));
            valid = false;
        } else {
            editText.setError(null);
        }

        return valid;
    }
}

