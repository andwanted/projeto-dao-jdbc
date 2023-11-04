package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.dbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO department " + "(Id,Name) "
					+ "VALUES " + "(?, ?)", Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, obj.getId());
			st.setString(2, obj.getName());
			
			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new dbException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new dbException(e.getMessage());
		} finally {
			DB.closeStatement(st);

		}

	}

	@Override
	public void update(Department obj) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE department "
					+ "SET Id = ?, Name = ? "
					+"WHERE Id = ?");
			
			st.setInt(1, obj.getId());
			st.setString(2, obj.getName());
			
			st.executeUpdate();

		}catch(SQLException e) {
			throw new dbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			
		}

	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		try {
			
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				conn.commit();
			}else {
				conn.rollback();
				throw new dbException("Erro! Impossivel deletar!");
			}
			
		}catch (SQLException e) {
			throw new dbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT department.Name AS DepName "
					+ "FROM department "
					+ "WHERE department.Id = ? ");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				
				Department obj = instantiateDepartment(rs);
				return obj;	
			}
			return null;
			
		}catch (SQLException e) {
			throw new dbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}
	


	private Department instantiateDepartment(ResultSet rs) throws SQLException   {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT department.Name AS DepName " 
					+ "FROM department " 
					+ "ORDER BY department.Name");
						
			rs = st.executeQuery();
			
			List<Department> list = new ArrayList<>();
			Map<Integer,Department > map = new HashMap<>();
			
			
			while(rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				
						if(dep == null) {
							dep = instantiateDepartment(rs);
							map.put(rs.getInt("DepartmentId"), dep);
						}
						
				Department obj = instantiateDepartment(rs);
				list.add(obj);
						
			}
			return list;
			
		}catch (SQLException e) {
			throw new dbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
