package OOP.Solution;

import OOP.Provided.OOPResult;

import java.util.Map;

public class OOPTestSummary {


    int success, failure, mismatch, error;

    public OOPTestSummary(Map<String, OOPResult> testMap) {
        for (OOPResult result : testMap.values()) {
            if (result.getResultType() == OOPResult.OOPTestResult.SUCCESS) success++;
            if (result.getResultType() == OOPResult.OOPTestResult.ERROR) error++;
            if (result.getResultType() == OOPResult.OOPTestResult.FAILURE) failure++;
            if (result.getResultType() == OOPResult.OOPTestResult.EXPECTED_EXCEPTION_MISMATCH) mismatch++;
        }

    }

    public int getNumSuccesses() {
        return success;
    }

    public int getNumFailures() {
        return failure;
    }

    public int getNumExceptionMismatches() {
        return mismatch;
    }

    public int getNumErrors() {
        return error;
    }
}
