package edu.cornell.slicktest;

import java.util.ArrayList;
import java.util.Stack;

public class MementoHandler extends Stack<Memento>
{
	
	public MementoHandler()
	{
		super();
	}
	
	public Memento pop()
	{
		if(!isEmpty())
		{
			return pop();
		}
		else
		{
			return null;
		}
	}
}
