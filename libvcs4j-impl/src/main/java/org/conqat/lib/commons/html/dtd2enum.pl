# dtd2enum
#
# This script is used to read a xhtml DTD and convert it to enum values 
# as needed in this package. It read from stdin and writes to stdout, so
# a typical invocation might look like this:
# $ perl dtd2enum.pl < xhtml1-frameset.dtd > enum.txt
#
# @author Benjamin Hummel
# @author $Author: deissenb $
# @version: $Rev: 10983 $
# @levd.rating YELLOW Rev: 10983

use strict;
use warnings;

# read file
my $all = "";
while (<>) { chomp; $all .= $_ . "\n"; }

# kill comments
$all =~ s/<!--.*?-->//gs; 

# resolve entities
my %ent = ();
while ($all =~ s/<!ENTITY +% *([\w.]+)\s+"([^""]*)"\s*>//s) 
{
   $ent{$1} = $2;
}
while ($all =~ s/%([^;]+);/$ent{$1}/gs) {}

# cleanup clutter
$all =~ s/<!ELEMENT[^>]+>//gs;
$all =~ s/\([^)]+\)//gs;
$all =~ s/"[^""]{1,40}"//gs;
$all =~ s/'[^'']{1,40}'//gs;
$all =~ s/[\#A-Z]//gs;
$all =~ s/(\s*\n)+/\n/gs;
$all =~ s/^\s+//gm;

# extract elements with attributes
my %attrs = ();
print "======== ELEMENTS ============\n\n";
while ($all =~ s/<!([^>]+)>//s) 
{
    my $tags = $1;
    $tags =~ s/[!<]//gs;
    $tags =~ s/^\s+//s;
    my @tok = split /\s+/, $tags;

    my $e = $tok[0];
    foreach (@tok) { $attrs{$_} = ""; tr/a-z:-/A-Z_/;  }
    print "/** The &lt;{\@linkplain \#$tok[0]}&gt; element. *" . 
	"/\n$tok[0] (\"$e\", ";
    shift @tok;
    foreach (@tok) { $_ = "EHTMLAttributes." . $_; }
    print join(", ", @tok) . "),\n\n";
}

# pretty print attributes
print "======== ATTRIBUTES ============\n\n";
foreach my $a (keys %attrs) {
    my $A = $a; $A =~ tr/a-z:-/A-Z_/;
    print "/** The {\@linkplain \#$A} attribute. *" . 
	"/\n$A (\"$a\"),\n\n";
}

