package oscript.varray;


public class ObjMultipleMapElement<KeyType,ValueType> implements MultiMapElement<KeyType,ValueType,ObjMultipleMapElement> {
		
		private KeyType  key;
		private ValueType val;
		private byte minMax=(byte)0;
		
		public ObjMultipleMapElement(KeyType key,ValueType val) {
			this.key=key;
			this.val=val;
		}
		
		public ValueType getValue() {
			return val;
		}
		
		public KeyType getKey() {
			return key;
		}

		public int compareTo(ObjMultipleMapElement o) {
			if (key == null && o.key == null)
				return 0;
			if (key == null)
				return -1;
			if (o.key == null)
				return 1;
			
			int r=key.getClass().hashCode()-o.key.getClass().hashCode();
			if (r != 0)
				return r;
			
			if (key instanceof Comparable) {
				if (key instanceof Number && o.key instanceof Number) 
				{
					if (key instanceof Long || key instanceof Integer) {
						long rr = ((Number)key).longValue()-((Number)o.key).longValue();
						if (rr < 0)
							r=-1;
						else if (rr > 0)
							r=1;						

					} else {
						double rr = ((Number)key).doubleValue()-((Number)o.key).doubleValue();
						if (rr < 0)
							r=-1;
						else if (rr > 0)
							r=1;						
					}
				}
				else
					r=((Comparable)key).compareTo(o.key);
			} else
			  r = key.hashCode()-o.key.hashCode();
			if (r < 0)
				return -1;
			if (r > 0)
				return 1;
			
			r = (minMax-o.minMax);
			if (r < 0)
				return -1;
			if (r > 0)
				return 1;
			
			if (val == null) {
				if (o.val == null) {
					return 0;
				} else {
					return -1;
				} 				
			} else {
				if (o.val == null) {
					return 1;
				}
			}
			
			r=val.getClass().hashCode()-o.val.getClass().hashCode();
			if (r != 0)
				return r;
			
			if (val instanceof Comparable) 
			{
				if (val instanceof Number && o.val instanceof Number) 
				{
					if (val instanceof Long || val instanceof Integer) 
					{
						long rr = ((Number)val).longValue() - ((Number)o.val).longValue();
						if (rr < 0)
							return -1;
						if (rr > 0)
							return 1;
						return 0;
					} else {
						double rr = ((Number)val).doubleValue() - ((Number)o.val).doubleValue();
						if (rr < 0)
							return -1;
						if (rr > 0)
							return 1;
						return 0;
					}
				}
				return ((Comparable)val).compareTo(o.val);
			}
			return val.hashCode()-o.hashCode();
		}
		
		
		public void setValue(ValueType val) {
			this.val=val;
		}
		
		public byte getMinMax() {
			return minMax;
		}
		public void setMinMax(byte t) {
			minMax=t;
		}

		public void setKey(KeyType key) {
			this.key=key;
		}

}
