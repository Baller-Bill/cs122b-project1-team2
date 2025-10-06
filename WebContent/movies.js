/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let moviesTableBodyElement = jQuery("#movies_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";

        /*
        *
        * jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("genres", three_genres);
        * */
        rowHTML += "<td>" + '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' + resultData[i]["movie_title"] + '</a>' + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["genres"] + "</td>";
        rowHTML += "<td>" + resultData[i]["stars"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
        /*
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        */
        rowHTML += "</tr>";


        // Append the row created to the table body, which will refresh the page
        moviesTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});