package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Director> listDirectors(Integer year){
		String sql = "SELECT DISTINCT * "
				+ "FROM `directors` "
				+ "WHERE `id` IN (	SELECT `director_id` "
				+ "					FROM `movies_directors` "
				+ "					WHERE `movie_id` IN (SELECT `id` "
				+ "									     FROM `movies` "
				+ "									     WHERE `year` = ?))";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int listActors(Director d1, Director d2, Integer year){
		String sql = "SELECT COUNT(DISTINCT r.`actor_id`) AS weight "
				+ "FROM `roles` r, `movies_directors` md1, `movies_directors` md2, roles r2 "
				+ "WHERE md1.`director_id` = ? AND md2.`director_id` = ? AND md2.`movie_id` = r2.`movie_id` AND md1.`movie_id` = r.`movie_id` "
				+ "AND md1.`movie_id` IN (SELECT id FROM `movies` WHERE `year` = ?) AND md2.`movie_id` IN (SELECT id FROM `movies` WHERE `year` = ?) AND r.`actor_id` = r2.`actor_id`";
		int result = 0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, d1.getId());
			st.setInt(2, d2.getId());
			st.setInt(3, year);
			st.setInt(4, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result = res.getInt("weight");
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
