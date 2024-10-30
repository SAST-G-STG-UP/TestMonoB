
public class VulnerableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vulnerable);

        String param = getIntent().getStringExtra("param");
        String query = "SELECT * FROM users WHERE username = '" + param + "'";
        // Execute the query using your database logic, e.g., SQLite
    }
}
