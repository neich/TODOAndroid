package org.udg.pds.todoandroid.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.databinding.ContentFavoritesBinding;
import org.udg.pds.todoandroid.databinding.FragmentImageBinding;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    private FragmentImageBinding binding;
    Uri selectedImage = null;


    TodoApi mTodoService;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        binding.bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        binding.bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    try {
                        InputStream is = getContext().getContentResolver().openInputStream(selectedImage);
                        String extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(selectedImage));
                        File tempFile = File.createTempFile("upload", extension, getContext().getCacheDir());
                        FileOutputStream outs = new FileOutputStream(tempFile);
                        IOUtils.copy(is, outs);
                        // create RequestBody instance from file
                        RequestBody requestFile =
                            RequestBody.create(
                                MediaType.parse(getContext().getContentResolver().getType(selectedImage)),
                                tempFile
                            );

                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                        Call<String> call = mTodoService.uploadImage(body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Image uploaded OK !!", Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(response.body()).into(binding.ivDownload);
                                }
                                else
                                    Toast.makeText(getContext(), "Response error !", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "Failure !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data != null && requestCode==1){
            selectedImage = data.getData();
            binding.ivPreview.setImageURI(selectedImage);
        }
    }

}
