/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");

    // append html created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Title: " + resultData["movie_title"] + "</p>" +
        "<p>Year: " + resultData["movie_year"] + "</p>" +
        "<p>Director: " + resultData["movie_director"] + "</p>" +
        "<p>Rating: " + resultData["movie_rating"] + "</p>");

    console.log("handleResult: populating genres from resultData");

    // Find the genres list
    let genresListElement = jQuery("#genres_list");

    // Append genres as list items
    let genres = resultData["genres"];
    for (let i = 0; i < genres.length; i++) {
        let genre = genres[i];
        genresListElement.append("<li>" + genre["genre_name"] + "</li>");
    }

    console.log("handleResult: populating stars table from resultData");

    // Populate the stars table
    // Find the empty table body by id "stars_table_body"
    let starsTableBodyElement = jQuery("#stars_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let stars = resultData["stars"];
    for (let i = 0; i < stars.length; i++) {
        let star = stars[i];

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><a href='single-star.html?id=" + star["star_id"] + "'>" + star["star_name"] + "</a></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starsTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by SingleMovieServlet
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
