package com.swagger.demo;

import com.swagger.demo.model.ApiModelSchema;
import com.swagger.demo.model.ModelField;
import com.swagger.demo.scanner.JavaSourceModelScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ModelScannerTest {

    private JavaSourceModelScanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = new JavaSourceModelScanner();
    }

    @Test
    @DisplayName("Test 1: Simple Model Parsing (User POJO)")
    public void testSimpleModel() {
        String sourceCode = "package com.bank.model;\n" +
                "public class User {\n" +
                "    private Long id;\n" +
                "    private String username;\n" +
                "    private String email;\n" +
                "    public Long getId() { return id; }\n" +
                "    public void setId(Long id) { this.id = id; }\n" +
                "    public String getUsername() { return username; }\n" +
                "    public void setUsername(String username) { this.username = username; }\n" +
                "}";

        ApiModelSchema schema = scanner.parseModelSourceCode(sourceCode);
        assertNotNull(schema);
        assertEquals("User", schema.getClassName());
        assertEquals("com.bank.model", schema.getPackageName());
        assertEquals(3, schema.getFields().size());

        Optional<ModelField> usernameField = schema.getField("username");
        assertTrue(usernameField.isPresent());
        assertEquals("String", usernameField.get().getType());
        assertTrue(usernameField.get().isHasGetter());
        assertTrue(usernameField.get().isHasSetter());
    }

    @Test
    @DisplayName("Test 2: Nested Model Reference Parsing (Investment POJO)")
    public void testNestedModel() {
        String sourceCode = "package com.bank.model;\n" +
                "import java.math.BigDecimal;\n" +
                "public class InvestmentPortfolio {\n" +
                "    private Long id;\n" +
                "    private MutualFund fundDetails;\n" +
                "    private BigDecimal units;\n" +
                "    public MutualFund getFundDetails() { return fundDetails; }\n" +
                "}";

        ApiModelSchema schema = scanner.parseModelSourceCode(sourceCode);
        assertNotNull(schema);

        Optional<ModelField> fundField = schema.getField("fundDetails");
        assertTrue(fundField.isPresent());
        assertEquals("MutualFund", fundField.get().getType());
        assertTrue(fundField.get().isNestedModel());
    }

    @Test
    @DisplayName("Test 3: List<Model> Generic Collection Parsing")
    public void testListModel() {
        String sourceCode = "package com.bank.model;\n" +
                "import java.util.List;\n" +
                "public class CustomerStatement {\n" +
                "    private String accountNumber;\n" +
                "    private List<Transaction> transactions;\n" +
                "    public List<Transaction> getTransactions() { return transactions; }\n" +
                "}";

        ApiModelSchema schema = scanner.parseModelSourceCode(sourceCode);
        assertNotNull(schema);

        Optional<ModelField> txnsField = schema.getField("transactions");
        assertTrue(txnsField.isPresent());
        assertEquals("List<Transaction>", txnsField.get().getType());
        assertEquals("Transaction", txnsField.get().getGenericType());
        assertTrue(txnsField.get().isCollection());
        assertTrue(txnsField.get().isNestedModel());
    }

    @Test
    @DisplayName("Test 4: BigDecimal High Precision Currency Type Parsing")
    public void testBigDecimalField() {
        String sourceCode = "package com.bank.model;\n" +
                "import java.math.BigDecimal;\n" +
                "public class LoanApplication {\n" +
                "    private BigDecimal principalAmount;\n" +
                "    private BigDecimal interestRate;\n" +
                "    private BigDecimal monthlyEmi;\n" +
                "}";

        ApiModelSchema schema = scanner.parseModelSourceCode(sourceCode);
        assertNotNull(schema);

        Optional<ModelField> principal = schema.getField("principalAmount");
        assertTrue(principal.isPresent());
        assertEquals("BigDecimal", principal.get().getType());
        assertFalse(principal.get().isCollection());
        assertFalse(principal.get().isNestedModel());
    }

    @Test
    @DisplayName("Test 5: Primitive Non-Nullable vs Wrapper Nullable Fields")
    public void testNullableFields() {
        String sourceCode = "package com.bank.model;\n" +
                "public class AccountConfig {\n" +
                "    private long accountId;\n" +
                "    private int durationMonths;\n" +
                "    private boolean active;\n" +
                "    private Integer priority;\n" +
                "    private String status;\n" +
                "}";

        ApiModelSchema schema = scanner.parseModelSourceCode(sourceCode);
        assertNotNull(schema);

        Optional<ModelField> accountId = schema.getField("accountId");
        assertTrue(accountId.isPresent());
        assertFalse(accountId.get().isNullable(), "Primitive long should NOT be nullable");

        Optional<ModelField> active = schema.getField("active");
        assertTrue(active.isPresent());
        assertFalse(active.get().isNullable(), "Primitive boolean should NOT be nullable");

        Optional<ModelField> priority = schema.getField("priority");
        assertTrue(priority.isPresent());
        assertTrue(priority.get().isNullable(), "Wrapper Integer SHOULD be nullable");

        Optional<ModelField> status = schema.getField("status");
        assertTrue(status.isPresent());
        assertTrue(status.get().isNullable(), "String object SHOULD be nullable");
    }

    @Test
    @DisplayName("Test 6: Real Project Source Code Model Directory Scanning")
    public void testProjectModelDirectoryScan() throws Exception {
        File modelDir = new File("../src/main/java/com/bank/model");
        if (!modelDir.exists()) {
            modelDir = new File("src/main/java/com/bank/model");
        }

        if (modelDir.exists()) {
            List<ApiModelSchema> schemas = scanner.scanModelDirectory(modelDir);
            assertFalse(schemas.isEmpty());
            assertTrue(schemas.stream().anyMatch(s -> "User".equals(s.getClassName())));
            assertTrue(schemas.stream().anyMatch(s -> "Account".equals(s.getClassName())));
            assertTrue(schemas.stream().anyMatch(s -> "Loan".equals(s.getClassName())));
            assertTrue(schemas.stream().anyMatch(s -> "Investment".equals(s.getClassName())));
        }
    }
}
