package com.deepexi.ds;

public class ModelException extends RuntimeException {

  public ModelException(String reason) {
    super(reason);
  }

  public ModelException(Throwable t) {
    super(t);
  }


  public static class TODOException extends ModelException {

    public TODOException(String feature) {
      super(String.format("TODO: [%s] not implement yet", feature));
    }

    public TODOException() {
      super("TODO");
    }
  }

  public static class UnsupportedException extends ModelException {

    public UnsupportedException(String msg) {
      super(String.format("UnsupportedException: %s", msg));
    }
  }

  public static class NoModelException extends ModelException {

    public NoModelException() {
      super("there is no model");
    }
  }

  public static class ModelNotFoundException extends ModelException {

    public ModelNotFoundException(String model) {
      super(String.format("Model [%s] not found", model));
    }
  }

  public static class ColumnNotExistException extends ModelException {

    public ColumnNotExistException(String table, String col) {
      super(String.format("column [%s] not exists in table [%s]", col, table));
    }
  }

  public static class ModelHasCycleException extends ModelException {

    public ModelHasCycleException() {
      super("model has cycle");
    }
  }

  public static class ModelHasManyRootException extends ModelException {

    public ModelHasManyRootException() {
      super("model has many root, not a single tree?");
    }
  }

  public static class FieldMissException extends ModelException {

    public FieldMissException(String missing) {
      super(String.format("required [%s] but missing", missing));
    }
  }

}
