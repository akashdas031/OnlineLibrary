package BookmarkService.ClientServiceDTOs.Enums;

import java.util.HashSet;
import java.util.Set;



public enum GENRE {

	FICTION,NON_FICTION,SCIENCE,HISTORY,FANTACY,MYSTERY,BIOGRAPHY,SELF_HELP,TECHNOLOGY;
	// Static Set for fast lookup
    private static final Set<String> VALID_GENRES = new HashSet<>();
    
    // Static block to populate the set
    static {
        for (GENRE genre : GENRE.values()) {
            VALID_GENRES.add(genre.name());
        }
    }

    // Check if a string is a valid enum value
    public static boolean isValidGenre(String value) {
        return value != null && VALID_GENRES.contains(value.toUpperCase());
    }
}
