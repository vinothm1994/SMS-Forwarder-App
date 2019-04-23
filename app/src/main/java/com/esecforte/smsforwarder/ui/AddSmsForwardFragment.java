package com.esecforte.smsforwarder.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.esecforte.smsforwarder.R;
import com.esecforte.smsforwarder.model.SMSForwardEntry;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSmsForwardFragment extends DialogFragment implements View.OnClickListener {


    private static final int SELECT_PHONE_NUMBER = 1011;
    public int id;
    boolean isSelectInCSmsNo = true;
    List<String> smsNos = new ArrayList<>();
    List<String> phoneNos = new ArrayList<>();
    private FlexboxLayout sms_ly;
    private FlexboxLayout phone_no_ly;
    private EditText sms_group_name_et;

    SMSForwardEntry mSmsForwardEntry;


    public AddSmsForwardFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_sms_forward, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View add_sms_no = view.findViewById(R.id.add_sms_no);
        Button cancel_btn = view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(this);
        Button save_btn = view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);

        sms_ly = view.findViewById(R.id.sms_ly);
        sms_group_name_et = view.findViewById(R.id.sms_group_name_et);
        phone_no_ly = view.findViewById(R.id.phone_no_ly);
        add_sms_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectInCSmsNo = true;
                showPickerDialog();
            }
        });
        View add_ph_no = view.findViewById(R.id.add_ph_no);
        add_ph_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectInCSmsNo = false;
                showPickerDialog();
            }
        });

        if (mSmsForwardEntry != null) {
            sms_group_name_et.setText(mSmsForwardEntry.getGroupName());
            id=mSmsForwardEntry.getId();
            for (String in : mSmsForwardEntry.getSmsNumbers()) {
                isSelectInCSmsNo = true;
                addPhoneNoView(in);
            }

            for (String in : mSmsForwardEntry.getForwardNumbers()) {
                isSelectInCSmsNo = false;
                addPhoneNoView(in);
            }


        }


    }

    void pick() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, SELECT_PHONE_NUMBER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContext().getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                number = number.replace(" ", "");
                addPhoneNoView(number);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addPhoneNoView(String number) {
        number = number.replace(" ", "");
        final Chip chip = (Chip) getLayoutInflater().inflate(R.layout.phone_no_item, sms_ly, false);
        chip.setChecked(true);
        chip.setCheckable(false);
        chip.setText(number);
        chip.setTag(number);
        final boolean temp = isSelectInCSmsNo;
        final String finalNumber = number;
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp) {
                    sms_ly.removeView(chip);
                    smsNos.remove(finalNumber);
                } else {
                    phone_no_ly.removeView(chip);
                    phoneNos.remove(finalNumber);
                }
            }
        });
        if (isSelectInCSmsNo) {
            if (smsNos.contains(number))
                return;

            sms_ly.addView(chip);
            smsNos.add(number);
            chip.setTag(R.id.add_ph_no, true);
        } else {
            if (phoneNos.contains(number))
                return;
            phone_no_ly.addView(chip);
            phoneNos.add(number);
            chip.setTag(R.id.add_ph_no, false);
        }

    }


    private void showPickerDialog() {

        final String[] items = new String[]{"Select from contact", "Enter Manually"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    pick();
                } else {
                    enterPhoneMauDialog();
                }


            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void enterPhoneMauDialog() {
        final Dialog alertDialog = new Dialog(getContext());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.user_ip_dialog);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        alertDialog.setCancelable(true);
        MaterialButton input1 = alertDialog.findViewById(R.id.cancel);
        final MaterialButton input2 = alertDialog.findViewById(R.id.continuee);
        final EditText titleDialog = alertDialog.findViewById(R.id.dialog_title);

        if (!isSelectInCSmsNo)
            titleDialog.setText("+91");
        titleDialog.requestFocus();
        input1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
       /* titleDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = titleDialog.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    input2.setEnabled(true);

                } else {
                    input2.setEnabled(false);
                }

            }
        });*/

        input2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = titleDialog.getText().toString().trim();
                int length = isSelectInCSmsNo?3:10;
                if (!TextUtils.isEmpty(text)&&text.length()>= length) {
                    alertDialog.dismiss();
                    addPhoneNoView(text);
                    titleDialog.setError(null);
                } else {
                    titleDialog.setError("Phone No Should be more than "+length+" characters");
                }
            }
        });
        alertDialog.show();

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_btn) {
            save();

        } else if (id == R.id.cancel_btn) {
            dismiss();
        }
    }

    private void save() {
        String groupName = sms_group_name_et.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(getContext(), "Enter Group Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (smsNos.isEmpty()) {
            Toast.makeText(getContext(), "Please add incoming sms numbers", Toast.LENGTH_SHORT).show();
            return;
        } else if (phoneNos.isEmpty()) {
            Toast.makeText(getContext(), "Please add forward sms numbers", Toast.LENGTH_SHORT).show();
            return;
        }


        if (mSmsForwardEntry == null) {
            SMSForwardEntry smsForwardEntry = new SMSForwardEntry(id, true, groupName, smsNos, phoneNos);
            ((MainActivity) getActivity()).addEntryData(smsForwardEntry);
        } else {
            mSmsForwardEntry.setGroupName(groupName);
            mSmsForwardEntry.setForwardNumbers(phoneNos);
            mSmsForwardEntry.setSmsNumbers(smsNos);
            ((MainActivity) getActivity()).edit();
        }

        dismiss();
    }


}
