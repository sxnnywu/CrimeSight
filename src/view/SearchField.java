package view;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("serial")
public class SearchField extends JPanel {
	
//	FIELDS --------------------------------------------------------------------------------------------------------
    private final JTextField textField;
    private final JPopupMenu suggestionPopup;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> suggestionListModel;
    private final Timer debounceTimer;
    private Consumer<String> searchAction;
    
//	Colors
    private final Color borderColor = new Color(200, 200, 200);
    private final Color focusBorderColor = new Color(52, 152, 219);
    private final Color placeholderColor = new Color(150, 150, 150);
    
// 	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
    public SearchField() {
    	
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        setPreferredSize(new Dimension(300, 40));
        
//        Search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        searchIcon.setForeground(placeholderColor);
        
//     	Text field
        textField = new JTextField();
        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setFont(new Font("Calibri", Font.PLAIN, 14));
        textField.setForeground(Color.BLACK);
        
//    	Clear button
        JButton clearButton = createClearButton();
        
//     	Add components
        add(searchIcon, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
        add(clearButton, BorderLayout.EAST);
        
//     	Suggestion popup
        suggestionPopup = new JPopupMenu();
        suggestionPopup.setBorder(BorderFactory.createLineBorder(borderColor));
        
        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        configureSuggestionList();
        
//     	Debounce timer
        debounceTimer = new Timer(300, e -> {
            if (searchAction != null) {
                searchAction.accept(textField.getText());
            }
            // Only show suggestions after debounce
            SwingUtilities.invokeLater(() -> {
                if (!textField.getText().isEmpty() && textField.isFocusOwner()) {
                    showSuggestions();
                } else {
                    suggestionPopup.setVisible(false);
                }
            });
        });
        setupListeners();
        suggestionPopup.setFocusable(false);
        suggestionList.setFocusable(false);
    }
    
//	CREATE CLEAR BUTTON -------------------------------------------------------------------------------------------
    private RoundedButton createClearButton() {
    	RoundedButton button = new RoundedButton("✕", placeholderColor, Color.WHITE);
        button.setFont(new Font("Calibri", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setVisible(false);
        
        button.addActionListener(e -> {
            textField.setText("");
            textField.requestFocus();
            suggestionListModel.clear();
            suggestionPopup.setVisible(false);
        });    
        return button;
    }
    
//	CONFIGURE SUGGESTION LIST -------------------------------------------------------------------------------------
    private void configureSuggestionList() {
    	
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFont(new Font("Calibri", Font.PLAIN, 14));
        suggestionList.setBackground(Color.WHITE);
        suggestionList.setFixedCellHeight(35);
        suggestionList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        suggestionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(new Color(240, 240, 240));
                    setForeground(Color.BLACK);
                } 
                else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }              
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });       
        suggestionList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = suggestionList.locationToIndex(e.getPoint());
                if (index != -1) {
                    suggestionList.setSelectedIndex(index);
                }
            }
        });      
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion();
                }
            }
        });      
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);
        suggestionPopup.add(scrollPane);
    }
    
// 	SET UP LISTENERS ----------------------------------------------------------------------------------------------
    private void setupListeners() {
        
//    	Focus listeners
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, focusBorderColor),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }         
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                suggestionPopup.setVisible(false);
            }
        });
        
//    	Document listener
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { 
                debounceTimer.restart();
            }
            public void insertUpdate(DocumentEvent e) { 
                debounceTimer.restart();
            }
            public void removeUpdate(DocumentEvent e) { 
                debounceTimer.restart();
            }
        });
        
//    	Key listener for navigation
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestionPopup.isVisible()) {
                    if (suggestionListModel.size() > 0) {
                        suggestionList.setSelectedIndex(0);
                        suggestionList.requestFocusInWindow();
                    }
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                    suggestionPopup.setVisible(false);
                    e.consume();
                }
            }
        });
//    	List selection listener
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && suggestionList.getSelectedValue() != null) {
                selectSuggestion();
            }
        });
    }
    
// 	SELECT SUGGESTION
    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();
        textField.setText(selected);
        suggestionPopup.setVisible(false);
        performSearch();
    }
    
// 	PERFORM SEARCH
    private void performSearch() {
        if (searchAction != null) {
            searchAction.accept(textField.getText());
        }
    }
    
// 	PUBLIC API METHOD
    public void setSearchAction(Consumer<String> searchAction) {
        this.searchAction = searchAction;
    }
    
// 	SET SUGGESTIONS
    public void setSuggestions(List<String> suggestions) {
        suggestionListModel.clear();
        suggestions.forEach(suggestionListModel::addElement);
        
        if (!suggestions.isEmpty() && textField.isFocusOwner()) {
            showSuggestions();
        } else {
            suggestionPopup.setVisible(false);
        }
    }
    
// 	SHOW SUGGESTIONS
    public void showSuggestions() {
        if (suggestionListModel.isEmpty() || !textField.isFocusOwner()) {
            if (suggestionPopup.isVisible()) {
                suggestionPopup.setVisible(false);
            }
            return;
        }
        
        // Only update if needed
        if (!suggestionPopup.isVisible()) {
            suggestionPopup.setPreferredSize(new Dimension(
                textField.getWidth(), 
                Math.min(200, suggestionList.getPreferredScrollableViewportSize().height)
            ));
            
            // Show the popup in the event dispatch thread
            SwingUtilities.invokeLater(() -> {
                suggestionPopup.show(textField, 0, textField.getHeight());
                suggestionPopup.pack();
            });
        }
    }
    
// 	GET TEXT
    public String getText() {
        return textField.getText();
    }
    
// 	SET PLACE HOLDER
    public void setPlaceholder(String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(placeholderColor);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(placeholderColor);
                }
            }
        });
    }

// 	GETTERS 
	public JTextField getTextField() {
		return textField;
	}

	public JPopupMenu getSuggestionPopup() {
		return suggestionPopup;
	}

	public JList<String> getSuggestionList() {
		return suggestionList;
	}

	public DefaultListModel<String> getSuggestionListModel() {
		return suggestionListModel;
	}

	public Timer getDebounceTimer() {
		return debounceTimer;
	}

	public Consumer<String> getSearchAction() {
		return searchAction;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public Color getFocusBorderColor() {
		return focusBorderColor;
	}

	public Color getPlaceholderColor() {
		return placeholderColor;
	}
    
    
}