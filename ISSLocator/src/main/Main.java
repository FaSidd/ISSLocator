package main;

import locator.ISSLocator;
import locator.OpenNotifyWebService;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
  static List<Point.Double> parseCoordinateString(String arg) {
    final Pattern COORDINATE_PATTERN = Pattern.compile("<(\\-?\\d+(\\.\\d+)?)\\s*,\\s*(\\-?\\d+(\\.\\d+)?)>");
    Matcher matcher = COORDINATE_PATTERN.matcher(arg);
    Set<Point.Double> coordinates = new HashSet<>();
    while (matcher.find()) {
      coordinates.add(new Point2D.Double(Double.parseDouble(matcher.group(1)), Double.parseDouble(matcher.group(3))));
    }
    return new ArrayList<>(coordinates);
  }

  static void processCoordinates(ISSLocator locator, List<Point2D.Double> coordinates) {
    for (Point.Double coordinate : coordinates) {
      String flyOverTime = null;
      try {
        flyOverTime = locator.computeTimeOfFlyOver(coordinate.x, coordinate.y);
      } catch (Exception e) {
        flyOverTime = e.getMessage();
      }
      System.out.printf("Localtime of flyover at <%f, %f> -> %s\n", coordinate.x, coordinate.y, flyOverTime);
    }
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException(
        "Need Coordinates arguments in the form <N.N, N.N>"
      );
    }

    List<Point.Double> coordinates = parseCoordinateString(args[0]);
    ISSLocator locator = new ISSLocator(new OpenNotifyWebService());
    processCoordinates(locator, coordinates);
  }
}
