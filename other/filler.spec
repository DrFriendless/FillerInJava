%define name filler
%define version 1.01
%define release 1
%define prefix /usr/local
%define rname %{name}-%{version}

Summary: Filler: a graphical game.
Name: %{name}
Version: %{version}
Release: %{release}
Copyright: GPL
URL: http://sourceforge.net/projects/filler
Group: Amusements/Games
Source: http://download.sourceforge.net/filler/%{rname}.tgz
Vendor:  John Farrell <Friendless@users.sourceforge.net>
Packager: John Farrell <Friendless@users.sourceforge.net>
Prefix: %{prefix}
#BuildRoot: /var/tmp/filler-build

%description
Filler is a graphical games where you occupy coloured hexes by changing
colours.

%prep
%setup -n %{name}

#[ "$RPM_BUILD_ROOT" != "/" ] && [ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT;

%build

make

%install

#[ "$RPM_BUILD_ROOT" != "/" ] && [ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT;

make install FILLERPATH=%{prefix}/bin DEST=%{prefix}/filler

%clean
#[ "$RPM_BUILD_ROOT" != "/" ] && [ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT;

%files
%{prefix}/filler/*
%{prefix}/bin/filler

%changelog
* Wed Dec 06 2000 John Farrell
- Passed FILLERPATH and DEST parameters to Makefile

* Sat Dec 02 2000 Tognon Stefano <ice00@users.sourceforge.net>
- Wrote first version of spec file.
