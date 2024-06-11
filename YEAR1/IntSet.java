class IntSet{

    private int [] data;
    private int MAX;
    private int count;

    public IntSet(){
	MAX = 32;
	data = new int[MAX];
	count =0;
    }

    public IntSet(int n){
	MAX = n;
	data = new int[MAX];
	count = 0;
    }

    // no duplicate values allowed
    // what happens if our array is full?
    public boolean add(int x){
	if(hasElement(x)) return true;
	if(count<MAX){
	    data[count]=x;
	    count++;
	    return true;
	}
	else{ // could resize the array instead to make bigger, then add
	    return false;
	}
    }

    public boolean hasElement(int x){
	if(count==0) return false;
	for(int i=0;i<count;i++)
	    if(data[i]==x) return true;
	return false;
    }

    // there is a better way to remove() ... can you implement it?
    public void remove(int x){
	if(! hasElement(x))return;
	if(data[count-1]==x){count--; return;}
	int i=0;
	while(data[i]!=x)i++;
	for(int j=i;j<count;j++)
	    data[j]=data[j+1];
	count--;
    }

    // Not typically included as an operation, but very useful for testing
    public void printSet(){
	for(int i=0;i<count;i++)
	    System.out.println("set["+i+"]="+data[i]);
    }

    // getter() method for present capacity of the set
    private int maxCapacity(){
	return MAX;
    }

    // This would be improved by adding a check that "other" is not null before calling its hasElement()
    // If we automatically increase the size our array as needed then we can do away with the capacity checks
    public IntSet intersection(IntSet other){
	IntSet result = new IntSet((maxCapacity()<other.maxCapacity())? maxCapacity():other.maxCapacity());
	for(int i=0;i<count;i++)
	    if(other.hasElement(data[i]))
		result.add(data[i]);
	return result;
    }

    // YOU implement this one :-)
    public IntSet union(IntSet other){
	IntSet result = null;
	return result;
    }
}
