package com.zealocity.pocketloancalculator;

import java.util.Calendar;

import com.zealocity.baseActivityAmazon;
import com.zealocity.pocketloancalculator.util.SystemUiHider;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends baseActivityAmazon
{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	//private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	
	EditText LoanAmount;
	EditText InterestRate;
	EditText PaymentAmount;
	EditText NumberPayments;
	EditText PayoffDate;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		// Set up an instance of SystemUiHider to control the system UI for this activity.
		//mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, 0);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		mSystemUiHider.show();
		
		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

		
    	Button b = (Button)findViewById(R.id.buttonCalculate);
	    b.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	calculateLoanPayments();
		            }
	         });
	    
    	Button exit = (Button)findViewById(R.id.dummy_button);
    	exit.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	exit();
		            }
	         });
              
        super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		if(AUTO_HIDE)
			delayedHide(100);
	}

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

	private void exit() {
    	super.finish();
    	this.finishActivity(0);
    	this.finish();
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	
	private void calculateLoanPayments() {
/*
 * loan calculations
 *    http://oakroadsystems.com/math/loan.htm
 */
		LoanAmount = (EditText)findViewById(R.id.editTextLoanAmount);
		InterestRate = (EditText)findViewById(R.id.editTextInterestRate);
		PaymentAmount = (EditText)findViewById(R.id.editTextPaymentAmount);
		NumberPayments = (EditText)findViewById(R.id.editTextNumberPayments);
		PayoffDate = (EditText)findViewById(R.id.editTextPayoffDate);

		double loanAmount = Float.valueOf(LoanAmount.getText().toString());
		double interestRate = Float.valueOf(InterestRate.getText().toString());
		double paymentAmount = Float.valueOf(PaymentAmount.getText().toString());
		
		double monthlyInterest = (interestRate / 100) / 12;
		double amount = monthlyInterest * loanAmount / paymentAmount;
		amount = 1 - amount;
		amount = -Math.log(amount);
		double payment = 1 + monthlyInterest;
		payment = Math.log(payment);
		int numberPayments = Integer.valueOf(String.valueOf(Math.round(amount / payment)));

		NumberPayments.setText(String.valueOf(numberPayments));
		
	    Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, numberPayments);
        String payoffDate = String.valueOf(android.text.format.DateFormat.format("MM-dd-yyyy", cal.getTime()));
        
		PayoffDate.setText(payoffDate);
	}
	
}
