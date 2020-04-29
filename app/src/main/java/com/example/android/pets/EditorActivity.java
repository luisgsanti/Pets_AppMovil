/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

import Clases.Pet;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    Pet mascota;

    private String LOG_TAG = EditorActivity.class.getSimpleName();
    private PetDbHelper mDbHelper;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
        mDbHelper = new PetDbHelper(this);

        LlenarDatos();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.

        Bundle extras = getIntent().getExtras();
        String tipoMenu = extras.getString("tipoMenu");

        if(tipoMenu.equals("agregar")){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_update, menu);
        }

        return true;
    }

    public void LlenarDatos(){

        try{
            Intent intent = getIntent();
            String recivied_menssage = intent.getStringExtra(CatalogActivity.EXTRA_MESSAGE);
            PetDbHelper db = new PetDbHelper(getApplicationContext());


            //Toast.makeText(this,""+recivied_menssage, Toast.LENGTH_SHORT).show();
            mascota = db.buscar(""+recivied_menssage);

            mNameEditText.setText(mascota.nombre);
            mBreedEditText.setText(mascota.raza);
            mWeightEditText.setText(mascota.peso);
            mGenderSpinner.setSelection(Integer.parseInt(mascota.genero));

        }catch (Exception e){
            //Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Update(){
        Intent intent = getIntent();
        String recivied_menssage = intent.getStringExtra(CatalogActivity.EXTRA_MESSAGE);

        mascota.id = ""+recivied_menssage;
        mascota.nombre = mNameEditText.getText().toString().trim();
        mascota.raza = mBreedEditText.getText().toString().trim();
        mascota.peso = mWeightEditText.getText().toString();
        mascota.genero = ""+mGender;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, mascota.nombre);
        values.put(PetEntry.COLUMN_PET_BREED, mascota.raza);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, mascota.peso);

        long id = db.update(PetEntry.TABLE_NAME,values, "_ID='"+mascota.id+"'",null);
        if( id == -1 ) Toast.makeText(this,"Error with saving a Pet",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Se modifico la mascota", Toast.LENGTH_SHORT).show();
    }

    public void Borrar(){
        Intent intent = getIntent();
        String recivied_menssage = intent.getStringExtra(CatalogActivity.EXTRA_MESSAGE);
        int cod = Integer.parseInt(recivied_menssage);
        cod = cod +1;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.delete(PetEntry.TABLE_NAME,"_ID='"+mascota.id+"'",null);
        if( id == -1 ) Toast.makeText(this,"Error with saving a Pet",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Se Borro la mascota", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertPet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                Borrar();
                finish();
                // Do nothing for now
                return true;

            case R.id.action_update:
                Update();
                finish();
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet(){
        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        int weight = Integer.parseInt(mWeightEditText.getText().toString());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, name);
        values.put(PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

        Log.v(LOG_TAG, "Record saving...");
        // -1 Error
        long id = db.insert(PetEntry.TABLE_NAME, null, values);
        if( id == -1 ) Toast.makeText(this,"Error with saving a Pet",Toast.LENGTH_SHORT).show();
        else Toast.makeText(this,"Pet saved!", Toast.LENGTH_SHORT).show();
    }
}