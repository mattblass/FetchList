# FetchList
This Android Kotlin app fetches a list from a remote url and renders it as a list showing the List ID and Name.

It has a simple data service component that uses Retrofit to query the data. 
There are 2 data classes, the raw data type matching the response format, and a Filtered class matching the business logic of the app.

This is using a HiltViewModel where data is processed as Flow data for use in the UI such that changes will update in a uniform way.
This includes some sorting functions to sort the data in different ways.

The UI renders a table like layout with a header and row. The header cells are clickable to change the sorting of the table.
The name field has a format of "Item N" and so the name is parsed so the sorting can be based on the name and the Int in the name.
