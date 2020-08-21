package br.com.copytagsfromyoutube;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import br.com.copytagsfromyoutube.task.BaseTask;
import br.com.copytagsfromyoutube.task.TaskRunner;
import br.com.copytagsfromyoutube.util.HttpRequestUtil;

public class FirstFragment extends Fragment {

    private EditText txtUrl;
    private TextView txtTags;
    private ClipboardManager clipboard;
    private TaskRunner runner;
    private RelativeLayout layoutLoading;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.runner = new TaskRunner();
        this.txtUrl = view.findViewById(R.id.txtUrl);
        this.txtTags = view.findViewById(R.id.txtTags);
        this.layoutLoading = view.findViewById(R.id.layoutLoading);

        Button btnDownloading = view.findViewById(R.id.btnDownloading);
        btnDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runner.executeAsync(new DownloadLinkImages(view));
            }
        });

        this.clipboard = (ClipboardManager) getActivity()
                .getSystemService(getActivity().CLIPBOARD_SERVICE);

        final String textoTags = view.getResources().getString(R.string.tags);
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_message);

        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText(dialog.getContext().getResources().getString(R.string.erro_copiar_tags));


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textoTags.equals(txtTags.getText().toString())) {
                    dialog.show();
                    ;
                } else {
                    ClipData clip = ClipData.newPlainText("label", txtTags.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    txtMessage.setText(view.getResources().getString(R.string.tags_copiadas));
                    dialog.show();
                }


            }
        });
    }

    private class DownloadLinkImages extends BaseTask {

        private View view;
        private String tags;

        public DownloadLinkImages(View view) {
            this.view = view;
        }

        @Override
        public void setUiForLoading() {
            layoutLoading.setVisibility(RelativeLayout.VISIBLE);
        }

        @Override
        public Object call() throws Exception {
            try {
                HttpRequestUtil httpRequest = new HttpRequestUtil(getContext(), txtUrl.getText().toString());
                if (httpRequest.isSucess()) {
                    this.tags = httpRequest.getResult();
                }
                return httpRequest.getResultCode();
            } catch (Exception e) {
                return 401;
            }
        }

        @Override
        public void setDataAfterLoading(Object result) {
            int resultCode = (int) result;
            layoutLoading.setVisibility(RelativeLayout.GONE);
            if (resultCode >= 200 && resultCode <= 300) {
                if (this.tags == null || this.tags.isEmpty()) {
                    txtUrl.setError(this.view.getResources().getText(R.string.erro_baixar_tags));
                } else {
                    txtTags.setText(this.tags);
                }
            } else {
                txtUrl.setError(this.view.getResources().getText(R.string.erro_baixar_tags));
            }
        }
    }
}