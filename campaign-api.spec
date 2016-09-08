Name            : campaign-api
Summary         : iTrueMart Scala source code
Version         : %{?version}%{!?version:1.0.1}
Release         : %{?release}%{!?release:0}

Group           : Applications/File
License         : (c)Douglas Gibbons

BuildArch       : %{?arch}%{!?arch:x86_64}
BuildRoot       : %{_tmppath}/%{name}-%{version}-root


# Use "Requires" for any dependencies, for example:
# Requires        : httpd

# Description gives information about the rpm package. This can be expanded up to multiple lines.
%description
iTrueMart Java source code


# Prep is used to set up the environment for building the rpm package
# Expansion of source tar balls are done in this section
%prep

# Used to compile and to build the source
%build

%pre

# The installation.
# We actually just put all our install files into a directory structure that mimics a server directory structure here
%install
rm -rf $RPM_BUILD_ROOT
install -d -m 755 $RPM_BUILD_ROOT/opt/apache-tomcat/apache-tomcat-8.0.23/webapps
cp ../SOURCES/*.war $RPM_BUILD_ROOT/opt/apache-tomcat/apache-tomcat-8.0.23/webapps/ROOT.war
cp ../SOURCES/*.txt $RPM_BUILD_ROOT/opt/apache-tomcat/apache-tomcat-8.0.23/webapps/version.txt

# Contains a list of the files that are part of the package
# See useful directives such as attr here: http://www.rpm.org/max-rpm-snapshot/s1-rpm-specref-files-list-directives.html
%files
/opt/apache-tomcat/apache-tomcat-8.0.23/webapps/ROOT.war
/opt/apache-tomcat/apache-tomcat-8.0.23/webapps/version.txt

%post
# If need to restart tomcat, do it here
chown -R tomcat8:tomcat8 /opt/apache-tomcat/apache-tomcat-8.0.23/webapps/version.txt
chown -R tomcat8:tomcat8 /opt/apache-tomcat/apache-tomcat-8.0.23/webapps/ROOT.war
chown -R tomcat8:tomcat8 /opt/apache-tomcat/apache-tomcat-8.0.23/webapps/ROOT

%preun
if [ "$1" = "1" ]; then
  echo "preun ==> for upgrade"
elif [ "$1" = "0" ]; then
  echo "preun ==> for uninstall"
  rm -rf /opt/apache-tomcat/apache-tomcat-8.0.23/webapps/ROOT.war
  rm -rf /opt/apache-tomcat/apache-tomcat-8.0.23/webapps/version.txt
fi

%postun
if [ "$1" = "1" ]; then
  echo "postun ==> for upgrade"
elif [ "$1" = "0" ]; then
  echo "postun ==> for uninstall"
fi

# Used to store any changes between versions
%changelog
