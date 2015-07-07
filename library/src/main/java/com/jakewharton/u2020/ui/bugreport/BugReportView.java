package com.jakewharton.u2020.ui.bugreport;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.Strings;

import static butterknife.ButterKnife.findById;

public final class BugReportView extends LinearLayout {
 	EditText titleView;
	EditText descriptionView;
	CheckBox screenshotView;
	CheckBox logsView;

	public interface ReportDetailsListener {
		void onStateChanged(boolean valid);
	}

	private ReportDetailsListener listener;

	public BugReportView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void loadViews() {
		titleView = findById(this, R.id.title);
		descriptionView = findById(this, R.id.description);
		screenshotView = findById(this, R.id.screenshot);
		logsView = findById(this, R.id.logs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		loadViews();

		titleView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					titleView.setError(Strings.isBlank(titleView.getText()) ? "Cannot be empty." : null);
				}
			}
		});
		titleView.addTextChangedListener(new EmptyTextWatcher() {
			@Override public void afterTextChanged(Editable s) {
				if (listener != null) {
					listener.onStateChanged(!Strings.isBlank(s));
				}
			}
		});

		screenshotView.setChecked(true);
		logsView.setChecked(true);
	}

	public void setBugReportListener(ReportDetailsListener listener) {
		this.listener = listener;
	}

	public Report getReport() {
		return new Report(String.valueOf(titleView.getText()),
				String.valueOf(descriptionView.getText()), screenshotView.isChecked(),
				logsView.isChecked());
	}

	public static final class Report {
		public final String title;
		public final String description;
		public final boolean includeScreenshot;
		public final boolean includeLogs;

		public Report(String title, String description, boolean includeScreenshot, boolean includeLogs) {
			this.title = title;
			this.description = description;
			this.includeScreenshot = includeScreenshot;
			this.includeLogs = includeLogs;
		}
	}
}
