package es.alejandrtf.ejemplousofirebasedatabase.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import es.alejandrtf.ejemplousofirebasedatabase.R;
import es.alejandrtf.ejemplousofirebasedatabase.pojos.Lugar;
import es.alejandrtf.ejemplousofirebasedatabase.storage.implementaciones.LugaresFirebase;
import es.alejandrtf.ejemplousofirebasedatabase.storage.implementaciones.UsuariosFirebase;
import es.alejandrtf.ejemplousofirebasedatabase.storage.interfaces.ILugaresAsync;
import es.alejandrtf.ejemplousofirebasedatabase.storage.interfaces.IUsuariosAsync;
import es.alejandrtf.ejemplousofirebasedatabase.pojos.Usuario;
import es.alejandrtf.ejemplousofirebasedatabase.utilities.FirebaseUIHelper;

public class MainActivity extends AppCompatActivity {
    public static final String LISTA_UI="UI";
    public static final String LISTA_SDK="SDK";
    public static final String EXTRA_TIPO_LISTA="extra_tipo_lista";

    private EditText etUid, etInicioSesion, etUidLeer,
            etUidGuardar, etNombreUsuario, etEmailUsuario;
    private TextView tvInicioSesion, tvEmail;

    private IUsuariosAsync usuarios; //operaciones con usuarios
    private ILugaresAsync lugares; //operaciones con lugares

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initReferences();


        //FIREBASE
        usuarios = new UsuariosFirebase();
        lugares=new LugaresFirebase(new FirebaseUIHelper());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_listaFirebasUI:
                lanzarActivityListaUsuarios(LISTA_UI);
                return true;
            case R.id.action_listaFirebasSDK:
                lanzarActivityListaUsuarios(LISTA_SDK);
                return true;
            case R.id.action_peores_lugares:
                mostrarAlertaPeoresLugares();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    //region MIS MÉTODOS

    private void initReferences() {
        etInicioSesion = findViewById(R.id.etInicioSesion);
        etUid = findViewById(R.id.etUidUsuario);
        etUidLeer = findViewById(R.id.etUidUsuarioLeer);
        tvInicioSesion = findViewById(R.id.tvValorInicioSesion);
        tvEmail = findViewById(R.id.tvValorEmail);
        etUidGuardar = findViewById(R.id.etUidUsuarioGuardar);
        etNombreUsuario = findViewById(R.id.etNombreUsuarioGuardar);
        etEmailUsuario = findViewById(R.id.etEmailGuardar);

    }

    private void lanzarActivityListaUsuarios(String tipo){
        Intent i=new Intent(this,ListaUsuariosActivity.class);
        i.putExtra(EXTRA_TIPO_LISTA,tipo);
        startActivity(i);
    }


    /** Ejecuta la consulta que obtiene los peores lugares
     * (con valoración menor que 3)
     */
    private void mostrarAlertaPeoresLugares(){
        lugares.getPeoresLugares(new ILugaresAsync.EscuchadorGetPeoresLugares() {
            @Override
            public void onRespuesta(List<Lugar> lugaresLista) {
                crearAlertaNombreLugares(lugaresLista);
            }

            @Override
            public void onError(DatabaseException e) {
                Log.e("Ej consulta lugares", "Error al leer.", e);

            }
        });

    }


    private void crearAlertaNombreLugares(List<Lugar>listaLugares){
        String message="";
        for (Lugar lugar :
                listaLugares) {
            message+=lugar.getNombre()+"\n";
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle("Lugares con valoración media inferior a 3")
                .setMessage(message)
                .setIcon(android.R.drawable.stat_notify_error)
                .setNeutralButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    //BOTONES PANTALLA
    public void actualizarInicioSesion(View v) {
        usuarios.actualizarInicioSesion(etUid.getText().toString(), Long.parseLong(etInicioSesion.getText().toString()));
    }

    public void leerInicioSesion(View v) {
        usuarios.leerInicioSesion(etUidLeer.getText().toString(),
                new IUsuariosAsync.EscuchadorLeerInicioSesion() {
                    @Override
                    public void onRespuesta(Long inicioSesion) {
                        Log.d("Ejemplo lectura", "Inicio Sesión = " + inicioSesion);
                        tvInicioSesion.append(inicioSesion + "\n");
                    }

                    @Override
                    public void onError(DatabaseException e) {
                        Log.e("Ejemplo lectura", "Error al leer.", e);
                    }
                });
    }


    public void leerEmailOnceTime(View v) {
        usuarios.leerEmail(etUidLeer.getText().toString(),
                new IUsuariosAsync.EscuchadorLeerEmail() {
                    @Override
                    public void onRespuesta(String email) {
                        Log.d("Ejemplo lectura", "Email = " + email);
                        tvEmail.append(email + "\n");
                    }

                    @Override
                    public void onError(DatabaseException e) {
                        Log.e("Ejemplo lectura", "Error al leer.", e);
                    }
                });
    }


    public void guardarUsuario(View v) {
        usuarios.guardarUsuario(etUidGuardar.getText().toString(),
                etNombreUsuario.getText().toString(),
                etEmailUsuario.getText().toString());
    }


    public void leerUsuario(View v) {
        usuarios.leerUsuario(etUidGuardar.getText().toString(),
                new IUsuariosAsync.EscuchadorLeerUsuario() {
                    @Override
                    public void onRespuesta(Usuario usuario) {
                        Log.d("Ejemplo lectura objeto", usuario.toString());
                        etNombreUsuario.setText(usuario.getNombre());
                        etEmailUsuario.setText(usuario.getCorreo());
                    }

                    @Override
                    public void onError(DatabaseException e) {
                        Log.e("Ejemplo lectura objeto", "Error al leer.", e);

                    }
                });
    }
    //endregion

}
