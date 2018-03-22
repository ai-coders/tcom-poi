package net.aicoder.tcom.poi.util;

/**
 * @since POI 3.15 beta 3
 */
public enum CellType {
    /**
     * Unknown type, used to represent a state prior to initialization or the
     * lack of a concrete type.
     * For internal use only.
     */
    //@Internal(since="POI 3.15 beta 3")
    _NONE(-1),

    /**
     * Numeric cell type (whole numbers, fractional numbers, dates)
     */
    NUMERIC(0),
    
    /** String (text) cell type */
    STRING(1),
    
    /**
     * Formula cell type
     * @see FormulaType
     */
    FORMULA(2),
    
    /**
     * Blank cell type
     */
    BLANK(3),
    
    /**
     * Boolean cell type
     */
    BOOLEAN(4),
    
    /**
     * Error cell type
     * @see FormulaError
     */
    ERROR(5);

    /**
     * @since POI 3.15 beta 3
     * deprecated POI 3.15 beta 3
     */
    private final int code;
    
    /**
     * @since POI 3.15 beta 3
     * deprecated POI 3.15 beta 3
     */
    private CellType(int code) {
        this.code = code;
    }
    
    /**
     * @since POI 3.15 beta 3.
     * deprecated POI 3.15 beta 3. Used to transition code from <code>int</code>s to <code>CellType</code>s.
     */
    public static CellType forInt(int code) {
        for (CellType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CellType code: " + code);
    }
    
    /**
     * @since POI 3.15 beta 3
     * deprecated POI 3.15 beta 3
     */
    public int getCode() {
        return code;
    }
    
}
