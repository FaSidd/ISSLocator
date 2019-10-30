# ISSLocator

- Based off of given GPS coordinates, program will tell you when the International Space Station will be above you.
- Created with partner for Software Design class
- Pulled ISS time at location from webservice API from this [JSON](http://api.open-notify.org/iss-pass.json?lat=29.721670&lon=-95.343631&n=1) file by implementing
  a JSON parser.
- Imported library from Maven to do time zone conversions from given latitude and longitude locally for better performance.
	- Library files can be found on [github](https://github.com/RomanIakovlev/timeshape).
- Created Unit Tests using JUnit to ensure application integrity
- Used Jenkins CI for building the project with gradle and testing the code.
