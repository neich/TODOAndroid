package org.udg.pds.todoandroid.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.udg.pds.todoandroid.R;

/**
 * Created with IntelliJ IDEA.
 * User: imartin
 * Date: 28/03/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

// This is the Login fragment where the user enters the username and password and
// then a RESTResponder_RF is called to check the authentication
public class Login extends Fragment {

    // We will use this to call back the container activity on response from REST calls
    private OnLoginListener mCallback;

    // This is the interface that the container activity has to implement
    // in order to use this fragment
    public interface OnLoginListener {
        public void checkCredentials(String username, String password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login, container, false);
        Button b = (Button) view.findViewById(R.id.login_button);
        // This is teh listener that will be used when the user presses the "Login" button
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText u = (EditText) view.findViewById(R.id.login_username);
                EditText p = (EditText) view.findViewById(R.id.login_password);
                mCallback.checkCredentials(u.getText().toString(), p.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLoginListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnLoginListener");
        }
    }
}
