package com.deepexi.ds.ast;

import com.deepexi.ds.ast.expression.Expression;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;

@Getter
public class OrderBy extends AstNode {

  // private final Identifier name;
  private final Expression name;
  private final OrderByDirection direction;

  public OrderBy(Expression name, OrderByDirection direction) {
    this.name = name;
    this.direction = direction;
  }

  public OrderBy(Expression name) {
    this.name = name;
    this.direction = OrderByDirection.ASC;
  }

  @Override
  public String toString() {
    return name + " " + direction.name;
  }

  @Override
  public <R, C> R accept(AstNodeVisitor<R, C> visitor, C context) {
    return visitor.visitOrderBy(this, context);
  }

  public enum OrderByDirection {
    ASC("asc"),
    DESC("desc");

    public final String name;

    public static OrderByDirection fromName(String name) {
      if (name == null) {
        return null;
      }
      String lowerName = name.toLowerCase();
      return Arrays.stream(OrderByDirection.values())
          .filter(x -> Objects.equals(x.name, lowerName))
          .findAny().orElse(null);
    }

    OrderByDirection(String name) {
      this.name = name;
    }
  }

}
