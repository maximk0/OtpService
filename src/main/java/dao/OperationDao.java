package dao;


import model.Operation;

import java.util.List;

public interface OperationDao {

    List<Operation> getAllOperations();
    Operation findByNumber(int operationNumber);
}