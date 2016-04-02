package io.jug6ernaut.debugdrawer.modules.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;
import io.jug6ernaut.debugdrawer.modules.data.LumberYard;
import io.jug6ernaut.debugdrawer.modules.model.LogEntry;
import io.jug6ernaut.debugdrawer.modules.util.Intents;

import java.io.File;


public class LogDialog extends AlertDialog {

    private final ListView listView;
    private final LogAdapter adapter;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public LogDialog(Context context) {
        super(context);

        adapter = new LogAdapter();

        listView = new ListView(context);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);

        setTitle("Logs");
        setView(listView);
        setButton(BUTTON_NEGATIVE, "Close", (dialog, which) -> {  /* no-op */ });
        setButton(BUTTON_POSITIVE, "Share", (dialog, which) -> { share(); });
    }

    @Override
    protected void onStart() {
        super.onStart();

        LumberYard lumberYard = LumberYard.getInstance(getContext());

        adapter.setLogs(lumberYard.bufferedLogs());

        lumberYard.setOnLogListener(logEntry -> {
            addLogEntry(logEntry);
            listView.setSelection(adapter.getCount() - 1);
        });
    }

    private void addLogEntry(final LogEntry logEntry) {
        handler.post(() -> adapter.addLog(logEntry));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LumberYard.getInstance(getContext()).setOnLogListener(null);
    }

    private void share() {
        LumberYard.getInstance(getContext())
                .save(new LumberYard.OnSaveLogListener() {
                    @Override
                    public void onSave(File file) {
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        Intents.maybeStartActivity(getContext(), sendIntent);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
