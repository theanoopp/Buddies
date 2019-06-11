package in.rgpvnotes.buddies.dialogs;


import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.DialogFragment;
import in.rgpvnotes.buddies.R;
import org.jetbrains.annotations.NotNull;


public class ViewImageDialog extends DialogFragment {

    private ImageView imageView;
    private String imageUrl;

    static ViewImageDialog newInstance(String imageUrl) {
        ViewImageDialog f = new ViewImageDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("image_url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.view_image_dialog, container, false);
    }







}
