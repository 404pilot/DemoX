# Init

## Block

1. Assign memory for `var` by finding its type
2. Assign `var` from top to bottom
3. Run constructor

Note that only assignment operation is allowed in the first block before declaring it.

	{
		s = "1"; // legal
		// s.charAt(0) // illegal
	}
	
	String s = "2";
	
