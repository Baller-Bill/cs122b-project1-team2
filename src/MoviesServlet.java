import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();


            //only the movies + ratings, need to somehow display the first 3 genres of each
            String query = "SELECT * " +
                    "FROM movies M JOIN ratings R ON M.id = R.movie_id " +
                    "ORDER BY R.rating DESC " +
                    "LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String director = rs.getString("director");
                String year = rs.getString("year");
                String rating = rs.getString("rating");

                String movie_id = rs.getString("id");

                String genreQuery = "SELECT G.name " +
                        "FROM genres_in_movies GM JOIN genres G ON GM.genre_id = G.id " +
                        "WHERE GM.movie_id = '" + movie_id + "' " +
                        "LIMIT 3;";

                Statement genre_statement = conn.createStatement();
                ResultSet genreRS = genre_statement.executeQuery(genreQuery);

                int genreIndex = 0;

                String three_genres = "";
                while (genreRS.next() && genreIndex < 3) {
                    String g = genreRS.getString("name");

                    if(genreIndex != 0){three_genres += ", ";}
                    three_genres += g;


                    genreIndex++;
                }
                if (genreIndex == 0) {three_genres = "N/A";}


                genreRS.close();
                genre_statement.close();

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("genres", three_genres);
                //jsonObject.add("genres", genreArray);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
