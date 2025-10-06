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
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// map to url
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Getting the movie info and rating
            String movieQuery = "SELECT m.title, m.year, m.director, r.rating FROM movies m LEFT JOIN ratings r ON m.id = r.movie_id WHERE m.id = ?";
            PreparedStatement movieStatement = conn.prepareStatement(movieQuery);
            movieStatement.setString(1, id);
            ResultSet rsMovie = movieStatement.executeQuery();

            if (!rsMovie.next()) {
                // move not found check
                JsonObject errorObject = new JsonObject();
                errorObject.addProperty("errorMessage", "Movie not found");
                out.write(errorObject.toString());
                response.setStatus(404);
                rsMovie.close();
                movieStatement.close();
                out.close();
                return;
            }

            String movieTitle = rsMovie.getString("title");
            String movieYear = rsMovie.getString("year");
            String movieDirector = rsMovie.getString("director");
            String movieRating = rsMovie.getString("rating");
            if (movieRating == null) movieRating = "N/A";

            rsMovie.close();
            movieStatement.close();

            // retrieve genres
            String genreQuery = "SELECT g.id as genre_id, g.name as genre_name FROM genres_in_movies gim JOIN genres g ON gim.genre_id = g.id WHERE gim.movie_id = ?";
            PreparedStatement genreStatement = conn.prepareStatement(genreQuery);
            genreStatement.setString(1, id);
            ResultSet rsGenres = genreStatement.executeQuery();

            JsonArray genresArray = new JsonArray();
            while (rsGenres.next()) {
                JsonObject genreObj = new JsonObject();
                genreObj.addProperty("genre_id", rsGenres.getString("genre_id"));
                genreObj.addProperty("genre_name", rsGenres.getString("genre_name"));
                genresArray.add(genreObj);
            }
            rsGenres.close();
            genreStatement.close();

            // retrieve stars
            String starQuery = "SELECT s.id as star_id, s.name as star_name FROM stars_in_movies sim JOIN stars s ON sim.star_id = s.id WHERE sim.movie_id = ?";
            PreparedStatement starStatement = conn.prepareStatement(starQuery);
            starStatement.setString(1, id);
            ResultSet rsStars = starStatement.executeQuery();

            JsonArray starsArray = new JsonArray();
            while (rsStars.next()) {
                JsonObject starObj = new JsonObject();
                starObj.addProperty("star_id", rsStars.getString("star_id"));
                starObj.addProperty("star_name", rsStars.getString("star_name"));
                starsArray.add(starObj);
            }
            rsStars.close();
            starStatement.close();

            // building the response
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_title", movieTitle);
            jsonObject.addProperty("movie_year", movieYear);
            jsonObject.addProperty("movie_director", movieDirector);
            jsonObject.addProperty("movie_rating", movieRating);
            jsonObject.add("genres", genresArray);
            jsonObject.add("stars", starsArray);

            // write JSON string to output
            out.write(jsonObject.toString());
            // set response status to 200
            response.setStatus(200);

        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // log error to local host
            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close(); // close db connection
        }
    }
}

