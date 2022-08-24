SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $SCRIPT_DIR
PROJECT_NAME=$(find '.' -name "build.sbt" \
  | head -n1 \
  | xargs grep '[ \t]*name :=' \
  | head -n1 \
  | sed 's/.*"""\(.*\)""".*/\1/')
VERSION=$(find '.' -name "build.sbt" \
  | head -n1 \
  | xargs grep '[ \t]*version :=' \
  | head -n1 \
  | sed 's/.*"\(.*\)".*/\1/')
echo "Starting build of project $PROJECT_NAME. Version $VERSION"
echo "> Temporary saving of develop files."
mkdir tmp
mkdir tmp/conf
mv conf/application.conf tmp/conf/
echo "> Copying production files..."
cp -r production-files/* .
echo "> Compiling and creating docker image..."
sbt compile docker:publishLocal
echo "> Docker image created."
echo "> Use 'docker run --env JAVA_OPTS=\"-Duser.timezone=UTC\" -p 80:9000 -it $PROJECT_NAME:$VERSION' to run it or,"
echo "> Use 'docker save $PROJECT_NAME:$VERSION > <filename>.tar' to export it"
echo "> Restoring develop files"
cp -rf tmp/* .
rm -rf tmp
echo "> Committing changes on app/views/index.scala.html (Build version of React App)"
git add app/views/index.scala.html
git commit -m "Updated compiled version of index.html of react app in Play Views. Version: $REACT_APP_VERSION"
echo "Build finished. Remember PUSH your branch to remote if needed."
