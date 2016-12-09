### RegexGazetter Plugin for the [GATE] (https://gate.ac.uk/) toolkit

This gazetteer is a modified version of GATE's Default Gazetteer that finds regular expressions in the text of a document in addition to litteral strings.


#### Initialization Parameters: 
  * same as in ANNIE Gazetteer, except for 'caseSensitive' that has been removed.
  
#### Runtime Parameters:
  * **addEntryFeature**: Whether or not the matched gazetteer entry should be added as a feature (name 'gazEntry') to the Lookup annotation. Default value: true.
  * **addStringFeature**: Whether or not the matched text should be added as a feature (name 'string') to the Lookup annotation. Default value: true.
  * **longestMatchOnly**: cf. parameter longestMatchOnly in ANNIE Gazetteer. Default value: true.
     
### NOTE
All the classes in this project are either a copy or a modified version of the classes in "gate.creole.gazetteer" package.
