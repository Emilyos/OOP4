package OOP.Solution;

import OOP.Provided.OOPResult;

public class OOPResultImpl implements OOPResult {
    private OOPTestResult result;
    private String message;

    public OOPResultImpl(OOPTestResult result, String message) {
        this.result = result;
        this.message = message;
    }

    @Override
    public OOPTestResult getResultType() {
        return result;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof OOPResult)) {
            return false;
        }
        OOPResult other = (OOPResult) obj;
        if ((getResultType() == null && other.getResultType() != null) ||
                getResultType() != null && (other.getResultType() == null)) return false;
        if(other.getMessage() == null || getMessage() == null){
            return other.getMessage() == null && getMessage() == null && other.getResultType() == getResultType();
        }
        return other.getMessage().equals(getMessage()) && other.getResultType() == getResultType();
    }
}
