# MATE-ML
**MATE-ML** is a **M**ethod for **A**utomatic **T**erm **E**xtraction based on **M**achine **L**earning.

This method treats the term extraction as a classification task, since the purpose of the extraction can be seen as classify candidates into terms or non-terms.
Figure below shows four steps of MATE-ML, which are completely automated and allow to adapt them depending on the application in which the extracted terms will be used.

![mate](https://cloud.githubusercontent.com/assets/10016650/7719226/99d0aa16-fe72-11e4-8dfe-74d0c7d4d8e2.png)

  Input: corpus, general language corpus (optional), external knowledge (corresponds to labeled words).  
  
  1. `Text preprocessing:` cleans and standardizes the input data, identifies POS (part-of-speech), remove stopwords, etc.  
  2. `Feature extraction:` calculates linguistic, statistical, and hybrid features that describe the words of input corpus.  
  3. `Filter application:` applies feature and attribute (words) selection.  
  4. `Classification of the candidate terms:` applies inductive or transductive classification algorithms in order to identify the terms.
  
Output: a list of *extracted terms*.

**Note:** The current version implements the first two steps:  
Text preprocessing: br.usp.mateml.steps.feature_extraction  
Feature extraction: br.usp.mateml.steps.preprocessing
