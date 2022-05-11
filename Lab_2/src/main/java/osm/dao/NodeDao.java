package osm.dao;

import osm.model.NodeDb;

import java.sql.SQLException;
import java.util.List;

public interface NodeDao {
    NodeDb getNode(long nodeId) throws SQLException;

    void insertNode(NodeDb node) throws SQLException;

    void insertPreparedNode(NodeDb node) throws SQLException;

    void batchInsertNodes(List<NodeDb> nodes) throws SQLException;
}
