# MetaProteomeAnalyzer (MPA) #

  * [News](#news)
  * [Downloads](#downloads)
  * [Introduction](#introduction)
  * [MPA Portable](#mpa-portable)
  * [Command Line Usage](#command-line-usage)
  * [Features](#features)
  * [System Requirements](#system-requirements)
  * [Conda Package](#conda-package)
  * [MPA GUI Startup](#mpa-gui-startup)

---

## MetaProteomeAnalyzer Publication:
  * [Muth _et al._: J Proteome Res. 2015 Mar 6;14(3):1557-65.](http://www.ncbi.nlm.nih.gov/pubmed/25660940).
  * If you use MetaProteomeAnalyzer as part of a publication, please include this reference.

---

## News ##

*MPA Portable version 1.7.0* has been released with the following new features:

* Added taxon-based two-step searching feature (in addition to the protein-based strategy).
* MapDB was updated to the latest version (ver. 3.0.5) to accelerate the peptide indexing.
* Peptide indexing is now available as optional feature on the command line.
* Disabled emPAI and NSAF quantification features in the GUI to accelerate the results retrieval.

---

*MPA Portable version 1.6.0* has been released with the following new features: 
* Added new import function for mzIdentML files: this feature supports the re-analysis of result identification data stored in mzIdentML format. 
 * Added new Import menu which contains the import function for "MPA Experiment" (see Release version 1.5.0). 
 * Added the feature to import MPA experiment files without requiring the corresponding MGF files.
 * Added the indexing of peptide sequences for FASTA databases: this feature allows to retrieve all protein identifications contained in the reference proteomes (and more than the default hits suggested by the search engines).
 
---

*MPA Portable version 1.5.0* has been released with the following updates:
* Added "Import MPA Experiment" feature: saved experiment files (*.mpa) can now be loaded into into a project
* Updated "Export MPA Experiment" feature: experiment files (*.mpa) are stored together with the original MS/MS spectra
* Added "Update UniProt Entries" feature (see MetaProteomeAnalyzerCLI for details)
* Various bugfixes and GUI improvements


## Downloads ##

[[Download software]](https://github.com/compomics/meta-proteome-analyzer/releases/download/v1.7.0/mpa-portable-1.7.0.zip)  *MPA Portable version 1.7.0 - Windows and Linux 32-bit/64-bit - released on August 18, 2017*

[[Download example data set]](https://github.com/compomics/meta-proteome-analyzer/raw/master/test/de/mpa/resources/Ebendorf1.zip)  *Microbial community sample data set (from Ebendorf biogas plant)*

[[Download example FASTA database]](https://github.com/compomics/meta-proteome-analyzer/raw/master/test/de/mpa/resources/fasta/uniprot_methanomicrobiales.fasta)  *Methanomicrobiales protein sequence database (UniProtKB FASTA format)*

[[Download MPA tutorial]](https://github.com/compomics/meta-proteome-analyzer/raw/master/docu/MPA_Portable_Tutorial.pdf)  *Guided MPA portable tutorial including graphical and command line interface usage*

---

## Introduction ##

**MetaProteomeAnalyzer** (MPA) is a scientific software for analyzing and visualizing metaproteomics (and also proteomics) data. The tool presents a MS/MS spectrum data processing application for protein identification in combination with an user-friendly interactive graphical interface. The metaproteomics data analysis software is developed in the Java programming language and provides various features for an user-defined querying of the results. In addition, MPA can also be executed on the command line (see below).

---

## MPA Portable ##

**MPA Portable** is a light-weight and stand-alone software for the identification of proteins and in-depth analysis of metaproteomics (and also proteomics) data. The MPA software currently supports the database search engines [Comet](http://comet-ms.sourceforge.net/), [MS-GF+](https://bix-lab.ucsd.edu/pages/viewpage.action?pageId=13533355) and [X!Tandem](http://www.thegpm.org/tandem/) taking MGF spectrum files as input data. User-provided FASTA databases (preferably downloaded from UniProtKB) are formatted automatically.

*Please note:* MPA Portable can be run directly on your desktop PC or laptop and no separate installation is needed: just double-click the provided JAR file or use the provided batch file (see below on this page). Otherwise, you can use the command line interface ([MetaProteomeAnalyzerCLI](https://github.com/compomics/meta-proteome-analyzer/wiki/MetaProteomeAnalyzerCLI)).

---

## Command Line Usage ##

MPA can also be used via the command line, see [MetaProteomeAnalyzerCLI](https://github.com/compomics/meta-proteome-analyzer/wiki/MetaProteomeAnalyzerCLI) for details.

--

## Features ##

The MPA metaproteomics software comes with the following key features:
  * Intuitive graphical user interface
  * Project management for MS/MS experiments
  * Shotgun protomics data analysis tool
  * Integrates the search algorithms Comet, MS-GF+ and X!Tandem
  * Import and re-analysis of results stored in mzIdentML data format: cross compatibility with various proteomics tools.
  * Detailed analysis of taxa, ontologies, pathways and enzymes
  * Grouping of redundant protein hits to so-called "meta-proteins" (protein groups)
  * Label-free quantification methods
  * Command line interface (for high-throughput processing tasks)
  * Interactive overview for each result set
  * Portable application 

---

## System Requirements ##
  * **Operating system**: (Tested on Windows XP, Windows Vista, Windows 7/8/10 and various Linux systems)
  * **Memory**: The more memory, the better! (preferably 4 GB at least, but highly recommended are 8 or 16 GB of RAM)

Please [download](https://github.com/compomics/meta-proteome-analyzer/releases/download/v1.7.0/mpa-portable-1.7.0.zip) the latest version of `mpa-portable-X.Y.Z.zip` (where X, Y and Z represent the current version of the software).

Before starting the MPA Portable version, please make sure that you have Java 1.8 installed. To check the currently installed java version, open a console/bash window and type:
```
java –version
```

If you haven't installed Java 1.8, please go directly to
[Java.com](http://www.java.com/download/) to download the latest Java version.

---

## Conda Package ##
MPA Portable can installed directly as [Miniconda](https://conda.io/miniconda.html) package from the bioconda channel:
```
conda install mpa-portable -c bioconda 
```

---

## MPA GUI Startup ##
After downloading the zip file, simply unzip the file and use the provided script from the built folder, i.e. mpa-portable.bat for Windows  or mpa-portable.sh for Linux (granting the access rights accordingly!).

You can also double-click on the JAR file, however this will give you no options to change the memory settings (see below).

Another possiblity is to use the command line directly:
```
java -jar mpa-portable-X.Y.Z.jar -XmxXXXXm 
```
**Note:** -Xmx*XXXX*m stands for maximum assigned memory (in megabytes of RAM)
