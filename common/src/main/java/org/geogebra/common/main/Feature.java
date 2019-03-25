package org.geogebra.common.main;

public enum Feature {
	ALL_LANGUAGES,

	LOCALSTORAGE_FILES,

	TUBE_BETA,

	EXERCISES,

	TOOL_EDITOR,

	DATA_COLLECTION,

	IMPLICIT_SURFACES,

	CONTOUR_PLOT_COMMAND,

	LOG_AXES,

	HIT_PARAMETRIC_SURFACE,

	PARAMETRIC_SURFACE_IS_REGION,

	ACRA,

	ANALYTICS,

	AV_DEFINITION_AND_VALUE,

	CONVEX_HULL_3D,

	/** GGB-334, TRAC-3401 */
	ADJUST_WIDGETS,

	/** GGB-944 */
	EXPORT_ANIMATED_PDF,

	/** GGB-776 */
	ABSOLUTE_TEXTS,

	/** MOB-788 */
	MOBILE_USE_FBO_FOR_3D_IMAGE_EXPORT,

	/** GGB-1263 */
	AUTOSCROLLING_SPREADSHEET,

	/** GGB-1252 */
	KEYBOARD_BEHAVIOUR,

	/** GGB-2336 */
	KEYBOARD_ATTACHED_TO_TABLET,

	/**
	 * GGB-1398 + GGB-1529
	 */
	SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME,

	/** MOW-97 */
	ERASER, ROUNDED_POLYGON,

	/** MOV-169 */
	DYNAMIC_STYLEBAR,

	MOW_PEN_EVENTS,

	/** GGB-1617 */
	AUTOMATIC_DERIVATIVES,

	/** SolveQuartic in CAS GGB-1635 */
	SOLVE_QUARTIC,

	/** MOW-88 */
	MOW_DIRECT_FORMULA_CONVERSION,

	EXPORT_SCAD_IN_MENU,

	EXPORT_COLLADA_IN_MENU,

	EXPORT_OBJ_IN_MENU,

	/** GGB-1916 */
	DEFAULT_OBJECT_STYLES,

	/** GGB-2008 */
	OBJECT_DEFAULTS_AND_COLOR,

	CENTER_STANDARD_VIEW,

	SURFACE_2D,

	/** GGB-1985*/
	FLOATING_SETTINGS,

	/** GGB-1986 */
	DIALOG_DESIGN,

	INITIAL_PORTRAIT,

	/** MOB-1293 */
	SELECT_TOOL_NEW_BEHAVIOUR,

	/** GGB-2183 change sin(15) -> sin(15deg) */
	AUTO_ADD_DEGREE,

	/** GGB-2222 change asin(0.5) -> asind(0.5) */
	CHANGE_INVERSE_TRIG_TO_DEGREES,

	/** MOB-1310 */
	SHOW_HIDE_LABEL_OBJECT_DELETE_MULTIPLE,

	/** MOB-1319 */
	MOB_NOTIFICATION_BAR_TRIGGERS_EXAM_ALERT_IOS_11,

	/** GGB-2215 */
	ARIA_CONTEXT_MENU,

	/** GGB-2394 */
	SPLITTER_LOADING,

	/** GGB-2144 */
	COMMAND_HOLES,

	WEB_CLASSIC_FLOATING_MENU,

	/** MOB-1537 */
	MOB_PREVIEW_WHEN_EDITING,

	/** GGB-2366 */
	TIKZ_AXES,

	/** AND-887 and IGR-732 */
	MOB_PROPERTY_SORT_BY,

	/** GGB-2375 */
	INPUT_BOX_LINE_UP_BETTER,

	/** MOB-1722 */
	MOB_EXPORT_STL,

	/** MOW-189 */
	MOW_TEXT_TOOL,

	/** GGB-2255 */
	GEOMETRIC_DISCOVERY,

	/** GGB-2542 */
	SYMBOLIC_AV,

	/** GGB-2517 */
	TOOLBAR_FROM_APPCONFIG,

	/** APPS-19 */
	TABLE_VIEW,

	/** G3D-42 */
	G3D_AR_REGULAR_TOOLS,

	/** G3D-95*/
 	G3D_AR_ROTATE_3D_VIEW_TOOL,

	/** G3D-97*/
	G3D_AR_TRANSLATE_3D_VIEW_TOOL,

	/** automatically add NDG conditions in locus equations */
	LOCUSEQU_AUTO_NDG,

	/** unify prove commands */
	PROVE_UNIFY,

	/** G3D-89 */
	G3D_IOS_FASTER_AV,

	/** Csilla Master */
	SPEECH_RECOGNITION,

	/** APPS-163 */
	KEY_HANDLER_EV3D,

	/** G3D-160 */
	G3D_AR_TARGET,

	/** G3D-170 */
	G3D_IMPROVE_SOLID_TOOLS,

    /** G3D-197 */
    G3D_TOOLS_SIMPLE_MORE_LESS,

    /** G3D-132 */
    G3D_SHOW_IN_ALGEBRA_VIEW,

	/** G3D-177 */
	G3D_STL_EXPORT_DIALOG,

	/** APPS-91 */
	LANG_PARAM_LAST,

	/** G3D-151 */
	G3D_IOS_NATIVE_AR,

	/** APPS-339 */
	SHOW_SAVE_AFTER_CLOSE_SEARCH,

	/** G3D-66 */
	G3D_IMPROVE_AUTOMATIC_ROTATION,

	/** APPS-450 */
	AV_INPUT_3DOT,

	/** G3D-266 */
	G3D_ARMotionEvent,

	/** G3D-272 */
	G3D_NEW_SURFACE_FUNCTIONS_COLORS,

	/** G3D-277 */
	G3D_AV_UPDATES_RELEVANT_GEOS_AND_PROPERTIES

}