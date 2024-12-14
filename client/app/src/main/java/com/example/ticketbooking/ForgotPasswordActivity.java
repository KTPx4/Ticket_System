package com.example.ticketbooking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import services.AccountHomeService;

public class ForgotPasswordActivity extends Activity {
    private EditText register_email;
    private Button register_button;
    private ImageButton back_button;
    private AccountHomeService accountHomeService;
    private TextView successMessage;  // Add a reference to the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        register_email = findViewById(R.id.register_email);
        register_button = findViewById(R.id.register_button);
        back_button = findViewById(R.id.back_button);
        successMessage = findViewById(R.id.success_message);  // Initialize the TextView

        accountHomeService = new AccountHomeService(this);

        back_button.setOnClickListener(v -> onBackPressed());

        register_button.setOnClickListener(v -> {
            String email = register_email.getText().toString().trim();

            // Validate email input
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Call resetAccount method
                accountHomeService.resetAccount(email, new AccountHomeService.ResponseCallback() {
                    @Override
                    public void onSuccess(String success) {
                        // Show success message and make the TextView visible
                        successMessage.setVisibility(View.VISIBLE);  // Show the success message
                        Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        // Handle failure response
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
