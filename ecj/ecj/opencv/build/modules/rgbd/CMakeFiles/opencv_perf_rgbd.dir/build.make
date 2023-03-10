# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.22

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:

# Disable VCS-based implicit rules.
% : %,v

# Disable VCS-based implicit rules.
% : RCS/%

# Disable VCS-based implicit rules.
% : RCS/%,v

# Disable VCS-based implicit rules.
% : SCCS/s.%

# Disable VCS-based implicit rules.
% : s.%

.SUFFIXES: .hpux_make_needs_suffix_list

# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/derek/Documents/thesis-ecj/ecj/opencv

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/derek/Documents/thesis-ecj/ecj/opencv/build

# Include any dependencies generated for this target.
include modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/depend.make
# Include any dependencies generated by the compiler for this target.
include modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/compiler_depend.make

# Include the progress variables for this target.
include modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/progress.make

# Include the compile flags for this target's objects.
include modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/flags.make

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/flags.make
modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o: /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_main.cpp
modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o -MF CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o.d -o CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o -c /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_main.cpp

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.i"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_main.cpp > CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.i

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.s"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_main.cpp -o CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.s

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/flags.make
modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o: /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_tsdf.cpp
modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/compiler_depend.ts
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -MD -MT modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o -MF CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o.d -o CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o -c /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_tsdf.cpp

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.i"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_tsdf.cpp > CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.i

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.s"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd/perf/perf_tsdf.cpp -o CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.s

# Object files for target opencv_perf_rgbd
opencv_perf_rgbd_OBJECTS = \
"CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o" \
"CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o"

# External object files for target opencv_perf_rgbd
opencv_perf_rgbd_EXTERNAL_OBJECTS =

bin/opencv_perf_rgbd: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_main.cpp.o
bin/opencv_perf_rgbd: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/perf/perf_tsdf.cpp.o
bin/opencv_perf_rgbd: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/build.make
bin/opencv_perf_rgbd: lib/libopencv_ts.a
bin/opencv_perf_rgbd: lib/libopencv_rgbd.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_highgui.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_calib3d.so.4.7.0
bin/opencv_perf_rgbd: 3rdparty/lib/libippiw.a
bin/opencv_perf_rgbd: 3rdparty/ippicv/ippicv_lnx/icv/lib/intel64/libippicv.a
bin/opencv_perf_rgbd: lib/libopencv_videoio.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_imgcodecs.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_features2d.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_flann.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_imgproc.so.4.7.0
bin/opencv_perf_rgbd: lib/libopencv_core.so.4.7.0
bin/opencv_perf_rgbd: modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/derek/Documents/thesis-ecj/ecj/opencv/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable ../../bin/opencv_perf_rgbd"
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/opencv_perf_rgbd.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/build: bin/opencv_perf_rgbd
.PHONY : modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/build

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/clean:
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd && $(CMAKE_COMMAND) -P CMakeFiles/opencv_perf_rgbd.dir/cmake_clean.cmake
.PHONY : modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/clean

modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/depend:
	cd /home/derek/Documents/thesis-ecj/ecj/opencv/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/derek/Documents/thesis-ecj/ecj/opencv /home/derek/Documents/thesis-ecj/ecj/opencv_contrib/modules/rgbd /home/derek/Documents/thesis-ecj/ecj/opencv/build /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd /home/derek/Documents/thesis-ecj/ecj/opencv/build/modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : modules/rgbd/CMakeFiles/opencv_perf_rgbd.dir/depend

