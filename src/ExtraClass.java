public class ExtraClass {

    private String index;
    private String toReturn;
    private String changeIndex;
    private String space;
    private String ui ="";



    public ExtraClass()
    {
        this("");
    }

    public ExtraClass(String variable)
    {
        setIndex(variable);
    }

    public void setIndex(String variable)
    {
        index = variable;
    }


    //Removes the index
    public void remove()
    {
        index ="";
    }


    //Adds the input String
    public void add(String variable)
    {
        index += variable;
    }

    //Adds an input string with specific size
    public void addIndexSize(int size, String variable)
    {
        changeIndex = variable;
        if(size - variable.length() <= 0)
        {
            changeIndex = changeIndex.substring(0,size);

        }
        else{

            space = "";
            for(int i=0; i< size - variable.length(); i++)
            {
                changeIndex += space;
            }
        }

        add(changeIndex);
    }



    // Method that stores the index in a temporary variable and clears the index of the variable 'index'
    public String passValue()
    {
        toReturn = index;
        remove();
        return toReturn;
    }


}
